package uk.nhs.digital.apispecs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static uk.nhs.digital.JcrNodeUtils.streamOf;

public class ApiSpecRepository {

    private static final Logger log = LoggerFactory.getLogger(ApiSpecRepository.class);

    private static final String WEBSITE_DOCS_FOLDER_JCR_PATH = "/jcr:root/content/documents/corporate-website";
    private static final String QUERY_STATEMENT =
        WEBSITE_DOCS_FOLDER_JCR_PATH + "//element(*, website:apispecification)/..[@jcr:primaryType='hippo:handle']";

    private final Session session;


    public ApiSpecRepository(final Session session) {
        this.session = session;
    }

    /**
     * @return All documents of type API Specifications found in the system or empty collection if none found; never
     * {@code null}.
     */
    public List<ApiSpecification> findAllApiSpecifications() {

        try {
            log.info("Looking for Api Specifications...");

            final QueryResult cmsSpecsHandleNodes = findApiSpecificationsHandleNodes();

            final List<ApiSpecification> apiSpecifications = createApiSpecificationsFrom(cmsSpecsHandleNodes.getNodes());

            log.info("Found {} Api Specifications.", apiSpecifications.size());

            return unmodifiableList(apiSpecifications);

        } catch (final Exception e) {
            throw new RuntimeException("Failed to find API Specification documents.", e);
        }
    }

    private List<ApiSpecification> createApiSpecificationsFrom(final NodeIterator cmsSpecsHandleNodes) {

        return streamOf(cmsSpecsHandleNodes)
            .map(DocumentLifecycleSupport::from)
            .map(ApiSpecification::from)
            .collect(toList());
    }

    private QueryResult findApiSpecificationsHandleNodes() {

        return executeJcrXpathQueryForQueryResult(QUERY_STATEMENT);
    }

    private QueryResult executeJcrXpathQueryForQueryResult(final String queryStatement) {
        try {
            return session
                .getWorkspace()
                .getQueryManager()
                .createQuery(queryStatement, Query.XPATH)
                .execute();

        } catch (final Exception e) {
            throw new RuntimeException(format("Failed to execute query %s.", queryStatement), e);
        }
    }
}

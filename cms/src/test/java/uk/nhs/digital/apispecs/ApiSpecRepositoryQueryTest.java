package uk.nhs.digital.apispecs;

import org.apache.sling.testing.mock.jcr.MockJcr;
import org.apache.sling.testing.mock.jcr.MockQueryResult;
import org.hippoecm.repository.util.JcrUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.digital.test.util.ReflectionTestUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static uk.nhs.digital.test.util.MockJcrRepoProvider.initJcrRepoFromYaml;


public class ApiSpecRepositoryQueryTest {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    private ApiSpecRepository apiSpecRepository;

    private Session session;

    @Before
    public void setUp() {

        session = MockJcr.newSession();

        apiSpecRepository = new ApiSpecRepository(session);
    }

    @Test
    public void findAllApiSpecifications_returnsAllSpecificationsPresentInTheSystem() {

        // given
        apiSpecsPresentInTheSystem();

        // when
        final List<ApiSpecification> actualApiSpecs = apiSpecRepository.findAllApiSpecifications();

        // then
        assertThat("All specifications present in the system have been found and returned",
            actualApiSpecs.size(),
            is(2)
        );
    }

    @Test
    public void findAllApiSpecifications_instantiatesSpecDocumentsInitialisedWithJcrHandleNode() throws Exception {

        // given
        apiSpecsPresentInTheSystem();

        // when
        final List<ApiSpecification> actualApiSpecs = apiSpecRepository.findAllApiSpecifications();

        // then
        final List<Node> actualDocumentHandleNodes = rootNodesFrom(actualApiSpecs);

        final Node firstDocumentHandleNode = actualDocumentHandleNodes.get(0);
        assertThat("First returned spec: initialised with node with correct path",
            firstDocumentHandleNode.getPath(),
            is("/content/documents/corporate-website/api-specifications-location-a/api-spec-a")
        );
        assertThat("First returned spec: initialised with node of correct type",
            JcrUtils.getStringProperty(firstDocumentHandleNode, "jcr:primaryType", "n/a"),
            is("hippo:handle")
        );

        final Node secondDocumentHandleNode = actualDocumentHandleNodes.get(1);
        assertThat("Second returned spec: initialised with node with correct path",
            secondDocumentHandleNode.getPath(),
            is("/content/documents/corporate-website/api-specifications-location-b/api-spec-b")
        );
        assertThat("Second returned spec: initialised with node of correct type",
            JcrUtils.getStringProperty(secondDocumentHandleNode, "jcr:primaryType", "n/a"),
            is("hippo:handle")
        );
    }

    @Test
    public void findAllApiSpecifications_returnsEmptyCollectionWhenNoApiSpecificationsFound() {

        // given
        noApiSpecificationsPresentInTheSystem();

        // when
        final List<ApiSpecification> actualApiSpecs = apiSpecRepository.findAllApiSpecifications();

        // then
        assertThat("No specifications were found and none was returned",
            actualApiSpecs,
            is(empty())
        );
    }

    @Test
    public void findAllApiSpecifications_usesJcrQueryTargetingApiSpecificatinDocsAnywhereWithinCorpWebsiteFolder() {

        // given
        final AtomicReference<String> actualQuery = givenJcrRepoCanAcceptQueries();

        // when
        apiSpecRepository.findAllApiSpecifications();

        // then
        assertThat(
            "Query targets handle nodes of documents of type 'website:apispecification' under Corporate Website folder",
            actualQuery.get(),
            is("/jcr:root/content/documents/corporate-website//element(*, website:apispecification)/..[@jcr:primaryType='hippo:handle']")
        );
    }

    @Test
    public void findAllApiSpecifications_throwsExceptionOnFailureToFindDocuments() {

        // given
        session = mock(Session.class);
        given(session.getWorkspace()).willThrow(new RuntimeException());

        expectedException.expectMessage("Failed to find API Specification documents.");
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(notNullValue(Exception.class));

        apiSpecRepository = new ApiSpecRepository(session);

        // when
        apiSpecRepository.findAllApiSpecifications();

        // then
        // expectations set up in 'given' are satisfied
    }

    private void noApiSpecificationsPresentInTheSystem() {
        MockJcr.setQueryResult(session, emptyList()); // JCR returns zero matches to any query
    }

    private AtomicReference<String> givenJcrRepoCanAcceptQueries() {

        final AtomicReference<String> actualQuery = new AtomicReference<>();

        MockJcr.addQueryResultHandler(session, query -> {
            actualQuery.set(query.getStatement());   // intercept actual query
            return new MockQueryResult(emptyList()); // results don't matter in this test but have to return something
        });

        return actualQuery;
    }

    private List<Node> rootNodesFrom(final List<ApiSpecification> actualApiSpecs) {

        return actualApiSpecs.stream()
            .map(apiSpecification -> ReflectionTestUtils.readField(apiSpecification, "documentLifecycleSupport"))
            .map(cmsDocumentProxy -> ReflectionTestUtils.readField(cmsDocumentProxy, "documentHandleNode"))
            .map(object -> (Node)object)
            .sorted(comparing(JcrUtils::getNodePathQuietly))
            .collect(toList());
    }

    private void apiSpecsPresentInTheSystem() {

        initJcrRepoFromYaml(session, "/test-data/api-specifications/ApiSpecRepositoryTest/api-specifications.yml");

        final Node rootNode = rootNodeOf(session);

        MockJcr.setQueryResult(session, asList(
            getNode(rootNode, "/content/documents/corporate-website/api-specifications-location-a/api-spec-a"),
            getNode(rootNode, "/content/documents/corporate-website/api-specifications-location-b/api-spec-b")
        ));
    }

    private Node getNode(final Node rootNode, final String jcrPath) {
        try {
            return rootNode.getNode(jcrPath);
        } catch (RepositoryException e) {
            throw new RuntimeException("Failed to obtain node " + jcrPath);
        }
    }

    private Node rootNodeOf(final Session session) {
        final Node rootNode;
        try {
            rootNode = session.getRootNode();
        } catch (RepositoryException e) {
            throw new RuntimeException("Failed to obtain root node from session", e);
        }
        return rootNode;
    }
}
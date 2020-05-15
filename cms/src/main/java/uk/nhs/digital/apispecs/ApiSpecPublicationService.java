package uk.nhs.digital.apispecs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.apispecs.dto.Content;

import java.util.ArrayList;
import java.util.List;

public class ApiSpecPublicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSpecPublicationService.class);

    private ApigeeService apigeeService;
    private ApiSpecRepository repository;

    public ApiSpecPublicationService(ApigeeService apigeeService, ApiSpecRepository repository) {
        this.apigeeService = apigeeService;
        this.repository = repository;
    }

    public void publish() {

        List<Content> contents = getApigeeSpecStatuses();   // apigee specs statuses
        List<CmsSpec> cmsSpecs = findApiSpecifications();   // cms spec documents

        LOGGER.info("============ content size =============  {}", contents.size());

        List<Content> specsToPublish = findSpecsToPublish(cmsSpecs,contents);

        publishSpecs(specsToPublish);
    }

    private List<Content> getApigeeSpecStatuses() {
        return apigeeService.apigeeSpecsStatuses();
    }

    private List<CmsSpec> findApiSpecifications() {
        return repository.apiSpecifications();
    }

    private List<Content> findSpecsToPublish(List<CmsSpec> cmsSpecs, List<Content> apigeeSpecStatuses) {
        LOGGER.debug("cms specs size: [{}], apigee specs size: [{}]", cmsSpecs.size(), apigeeSpecStatuses.size());
        LOGGER.info("Compare cms specs and apigee specs to find out specs to publish...");
        return new ArrayList<Content>();
    }

    private void publishSpecs(List<Content> specsToPublish) {
        LOGGER.info("Publishing the SPEC for specification-id: [{}]", specsToPublish.size());
    }
}

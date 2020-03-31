package uk.nhs.digital.intranet.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.CommonComponent;
import uk.nhs.digital.intranet.model.SearchResult;
import uk.nhs.digital.intranet.provider.GraphProvider;

import java.util.List;

public class SearchPageComponent extends CommonComponent {

    private static final String REQUEST_ATTR_RESULTS = "results";

    private final GraphProvider graphProvider;

    public SearchPageComponent(final GraphProvider graphProvider) {
        this.graphProvider = graphProvider;
    }

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        final String searchQuery = getSearchQuery(request);
        final List<SearchResult> results = graphProvider.getPeople(searchQuery);
        populateRequest(request, results);
    }

    private void populateRequest(final HstRequest request, final List<? extends SearchResult> results) {
        request.setAttribute(REQUEST_ATTR_QUERY, getSearchQuery(request));
        request.setModel(REQUEST_ATTR_RESULTS, results);
    }

    protected String getSearchQuery(HstRequest request) {
        return cleanupSearchQuery(getAnyParameter(request, REQUEST_PARAM_QUERY));
    }
}

package uk.nhs.digital.website.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@Node(jcrType = "website:apispecification")
@HippoEssentialsGenerated(internalName = "website:apispecification", allowModifications = false)
public class ApiSpecification extends CommonFieldsBean {

    public String getSpecificationid() {
        return getProperty("website:specification_id");
    }

    public String getHtml() {
        return getProperty("website:html");
    }
}
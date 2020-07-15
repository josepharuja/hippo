<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "macro/documentHeader.ftl">
<#include "macro/sections/sections.ftl">


<!DOCTYPE html>
<html lang="en" class="no-js">

<#include "app-layout-head.ftl">

<body>
<#-- @ftlvariable name="document" type="uk.nhs.digital.website.beans.ApiSpecification" -->

<#-- 'Page header' (banner with NHSD logo) above the 'document header' -->
<header class="site-header site-header--with-search" id="header">
    <div class="grid-wrapper grid-wrapper--full-width grid-wrapper--wide">
        <div class="grid-row">
            <div class="column column--reset">
                <div class="grid-wrapper grid-wrapper--collapse">
                    <div class="grid-row">
                        <div class="column column--18-75 column--reset">
                            <a class="site-header-a__logo" href="<@hst.link siteMapItemRefId='root'/>">
                                <img src="<@hst.webfile path="/images/nhs-digital-logo.svg"/>" alt="NHS Digital logo" class="site-header__logo"></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>


<#-- Actual content generated by Swagger UI -->
<#if document?? >
    <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/apispecification/swagger-ui.css"/>"/>

    <style type="text/css">

        /*
        Custom overrides of Swagger UI styles from swagger-ui.css.
        */

        /*
        Styles that hide sections that are redundant here because they are already present in the main document.
        */

        /* Swagger top-bar (logo + search box) */
        .swagger-container .topbar {
            display: none;
        }

        /* Swagger document info (inc. redundant title) */
        .swagger-ui .information-container {
            display: none;
        }

        /* API Overview section */
        .info .description {
            display: none;
        }

        /* Endpoints' Overview sections */
        .opblock-body .opblock-description-wrapper, .opblock-external-docs-wrapper {
            display: none;
        }

        .opblock-section .opblock-description-wrapper {
            display: block;
        }

        /* Redundant 'Responses' header under 'Execute' button */
        .responses-wrapper .opblock-section-header {
            display: none !important;
        }

        /* Endpoints' Responses (static redundant content, not actual 'live' responses for the sandbox servers) */
        .responses-table:not(.live-responses-table) {
            display: none;
        }

        /* Header of the above */
        div.responses-inner > div > h4 {
            display: none;
        }

        /* Hide Schemas at the bottom of the popup */
        .models {
            display: none !important;
        }

        /*
        Other overriding styles
        */

        /* Disables shadows around the 'servers selector' */
        .swagger-ui .scheme-container {
            box-shadow: none;
        }

        /* Fixes the look of table header values */
        .swagger-ui table thead tr th {
            padding-left: 6px;
            min-width: 150px;
        }

        /* Hides document's subtitle */
        .article-header__subtitle {
            display: none;
        }

        /* Prevents document's title from being wrapped */
        .local-header__title {
            white-space: nowrap;
        }

        /* Prevents the 'Download' button's label (response payload) from being wrapped */
        .download-contents {
            width: auto !important;
            right: 20px !important;
        }

        /* Adjusts spacing beween the 'Copy to clipboard' and 'Download' buttons */
        .copy-to-clipboard {
            right: 130px !important;
        }

    </style>
</#if>


<article class="article article--apispecification" itemscope>
    <@documentHeader document 'general' '' "Try this API: ${document.title}"></@documentHeader>

    <div class="grid-wrapper grid-wrapper--article">
        <div class="grid-row">

            <div id="content" aria-label="Document content">
                <#if document?? >
                    <div id="swagger-ui"></div>

                    <script src="<@hst.webfile path="/apispecification/swagger-ui-bundle.js"/>"></script>
                    <script src="<@hst.webfile path="/apispecification/swagger-ui-standalone-preset.js"/>"></script>

                    <script>
                        const specification = ${document.json?no_esc}

                            window.onload = function () {

                                const ui = SwaggerUIBundle({
                                    spec: specification,

                                    dom_id: '#content',
                                    deepLinking: true,
                                    presets: [
                                        SwaggerUIBundle.presets.apis,
                                        SwaggerUIStandalonePreset
                                    ],
                                    plugins: [
                                        SwaggerUIBundle.plugins.DownloadUrl
                                    ],
                                    layout: "StandaloneLayout"
                                })

                                window.ui = ui
                            }
                    </script>
                <#else>
                    <#assign section = { "emphasisType": "Important", "heading": "Open from the API Specification page", "bodyCustom": "Please open this feature via 'Try it now' button in the page of the API Specification."  } />
                    <@emphasisBox section=section />
                </#if>
            </div>
        </div>
    </div>
</article>

</body>
</html>

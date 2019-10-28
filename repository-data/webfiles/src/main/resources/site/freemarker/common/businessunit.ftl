<#ftl output_format="HTML">

<#-- @ftlvariable name="document" type="uk.nhs.digital.website.beans.BusinessUnit" -->

<#include "../include/imports.ftl">
<#include "macro/sections/sections.ftl">
<#include "macro/documentHeader.ftl">
<#include "macro/stickyNavSections.ftl">
<#include "macro/metaTags.ftl">
<#include "macro/component/lastModified.ftl">

<#-- Add meta tags -->
<@metaTags></@metaTags>

<#assign hasSummaryContent = document.summary.content?has_content />
<#assign hasSectionContent = document.sections?has_content />
<#assign sectionTitlesFound = countSectionTitles(document.sections) />
<#assign renderNav = (hasSummaryContent && sectionTitlesFound gte 1) || sectionTitlesFound gt 1 />

<article class="article article--businessunit">

    <@documentHeader document 'businessunit' ''></@documentHeader>

    <div class="grid-wrapper grid-wrapper--article">

        <div class="grid-row">
            <#if renderNav>
            <div class="column column--one-third page-block page-block--sidebar article-section-nav-outer-wrapper">
                <div id="sticky-nav">
                    <#assign links = [{ "url": "#top", "title": 'Top of page' }] />
                    <#if document.vision?has_content>
                      <#assign links += [{ "url": "#vision", "title": 'Vision' }] />
                    </#if>
                    <#if document.purposes?has_content>
                      <#assign links += [{ "url": "#purposes", "title": 'Purposes' }] />
                    </#if>
                    <#assign links += getStickySectionNavLinks({ "document": document, "includeTopLink": false, "ignoreSummary": true}) />
                    <@stickyNavSections links></@stickyNavSections>
                </div>
            </div>
            </#if>

            <div class="column column--two-thirds page-block page-block--main">

                <#if document.vision?has_content>
                  <div id="vision" class="article-section" data-uipath="website.businessunit.vision">
                      <h2>Vision</h2>
                      <@hst.html hippohtml=document.vision contentRewriter=gaContentRewriter />
                  </div>
                </#if>

                <#if document.purposes?has_content>
                  <div id="purposes" class="article-section" data-uipath="website.businessunit.purposes">
                      <h2>Purposes</h2>
                      <ul>
                        <#list document.purposes as purpose>
                          <li><@hst.html hippohtml=purpose contentRewriter=gaContentRewriter /></li>
                        </#list>
                      </ul>
                  </div>
                </#if>

                <#if hasSectionContent>
                    <@sections document.sections></@sections>
                </#if>

                <div class="article-section muted">
                    <@lastModified document.lastModified false></@lastModified>
                </div>
            </div>
        </div>
    </div>
</article>

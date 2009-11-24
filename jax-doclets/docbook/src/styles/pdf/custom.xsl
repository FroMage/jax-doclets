<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

  <xsl:import href="file:///@dbf.xsl@/fo/highlight.xsl"/>

  <!-- Activate Graphics -->
  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="admon.graphics.path">images/</xsl:param>
  <xsl:param name="admon.graphics.extension">.gif</xsl:param>
  <xsl:param name="callout.graphics" select="1" />
  <xsl:param name="callout.graphics.path">images/callouts/</xsl:param>
  <xsl:param name="callout.graphics.extension">.gif</xsl:param>

  <xsl:param name="table.borders.with.css" select="1"/>
  <xsl:param name="html.stylesheet">css/main.css</xsl:param>
  <xsl:param name="html.stylesheet.type">text/css</xsl:param>         
  <xsl:param name="generate.toc">book toc,title</xsl:param>         

  <xsl:param name="admonition.title.properties">text-align: left</xsl:param>

  <!-- Label Chapters and Sections (numbering) -->
  <xsl:param name="chapter.autolabel" select="1"/>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.autolabel.max.depth" select="1"/>

  <xsl:param name="section.label.includes.component.label" select="1"/>
  <xsl:param name="table.footnote.number.format" select="'1'"/>

<!-- Remove "Chapter" from the Chapter titles... -->
  <xsl:param name="local.l10n.xml" select="document('')"/>
  <l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
    <l:l10n language="en">
      <l:context name="title-numbered">
        <l:template name="chapter" text="%n.&#160;%t"/>
        <l:template name="section" text="%n&#160;%t"/>
      </l:context>
    </l:l10n>
  </l:i18n>
  
  <!--
  This section enables source highlighting and custom colors
  -->
  <xsl:param name="highlight.source" select="1"/>
  <xsl:output indent="no"/>
  <xsl:param name="highlight.default.language">java</xsl:param>

  <!--
  Ant will automatically replace @dbf.xsl@ with the path to
  the config at runtime
  -->
  <xsl:param name="highlight.xslthl.config">file:///@dbf.xsl@/highlighting/xslthl-config.xml</xsl:param>

<xsl:attribute-set name="monospace.verbatim.properties">
  <xsl:attribute name="font-size">8pt</xsl:attribute>
</xsl:attribute-set>


</xsl:stylesheet>

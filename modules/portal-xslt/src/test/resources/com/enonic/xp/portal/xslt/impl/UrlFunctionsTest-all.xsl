<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="urn:enonic:xp:portal:1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="portal:url('_path=a')"/>
      </value>
      <value>
        <xsl:value-of select="portal:url('_path=a','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:pageUrl('_path=some/path','a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:pageUrl('_path=some/path','a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:imageUrl('_id=123')"/>
      </value>
      <value>
        <xsl:value-of select="portal:imageUrl('_name=a','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:assetUrl('_path=a')"/>
      </value>
      <value>
        <xsl:value-of select="portal:assetUrl('_path=a','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:attachmentUrl('a=1')"/>
      </value>
      <value>
        <xsl:value-of select="portal:attachmentUrl('a=1','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:serviceUrl('_service=a')"/>
      </value>
      <value>
        <xsl:value-of select="portal:serviceUrl('_service=a','b=2')"/>
      </value>
      <value>
        <xsl:value-of select="portal:componentUrl('_component=a')"/>
      </value>
      <value>
        <xsl:value-of select="portal:componentUrl('_component=a','b=2')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>

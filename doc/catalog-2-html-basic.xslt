<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:madoc="http://www.lexml.gov.br/madoc/1.0">

<xsl:template match="/">
  <html>
  <body>
  <h2>MADOC Catalog</h2>
    <table border="1">
      <tr bgcolor="#9acd32">
        <th style="text-align:left">Tipo modelo</th>
        <th style="text-align:left">Titulo</th>
        <th style="text-align:left">Categorias</th>
        <th style="text-align:left">Indexação</th>
      </tr>
      <xsl:for-each select="madoc:Catalog/madoc:MadocDocuments/madoc:MadocDocument">
      <tr>
        <td><xsl:value-of select="madoc:Metadata/madoc:Entry[@key='TipoModelo']"/></td>
<td><xsl:value-of select="madoc:Metadata/madoc:Entry[@key='Titulo']"/></td>
        <td>
     <xsl:for-each select="madoc:Metadata/madoc:Entry[@key='Categoria']/madoc:List/madoc:Entry">
<xsl:value-of select="." />,
</xsl:for-each>
        </td>
        <td>
     <xsl:for-each select="madoc:Metadata/madoc:Entry[@key='Indexacao']/madoc:List/madoc:Entry">
<xsl:value-of select="." />,
</xsl:for-each>
        </td>
      </tr>
      </xsl:for-each>

    </table>

  </body>
  </html>
</xsl:template>
</xsl:stylesheet>

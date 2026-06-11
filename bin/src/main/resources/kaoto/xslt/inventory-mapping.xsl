<xsl:stylesheet version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">

        <inventory>

            <inventoryId>
                <xsl:value-of select="productId"/>
            </inventoryId>

            <itemName>
                <xsl:value-of select="productName"/>
            </itemName>

            <stock>
                <xsl:value-of select="quantity"/>
            </stock>

            <location>
                <xsl:value-of select="warehouseLocation"/>
            </location>

            <inventoryType>
                <xsl:value-of select="category"/>
            </inventoryType>

            <updatedTime>
                <xsl:value-of select="lastUpdated"/>
            </updatedTime>

        </inventory>

    </xsl:template>

</xsl:stylesheet>
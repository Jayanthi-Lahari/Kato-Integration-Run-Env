<xsl:stylesheet version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">

        <inventoryUpdate>

            <!-- Warehouse Details -->
            <warehouse>

                <warehouseId>
                    <xsl:value-of select="ObjectNode/warehouseId"/>
                </warehouseId>

                <warehouseName>
                    <xsl:value-of select="ObjectNode/warehouseName"/>
                </warehouseName>

                <location>
                    <xsl:value-of select="ObjectNode/location"/>
                </location>

                <manager>
                    <xsl:value-of select="ObjectNode/manager"/>
                </manager>

                <inventoryDate>
                    <xsl:value-of select="ObjectNode/inventoryDate"/>
                </inventoryDate>

            </warehouse>

            <!-- Product Inventory -->
            <inventoryItems>

                <xsl:for-each select="ObjectNode/products/item">

                    <inventoryItem>

                        <inventoryId>
                            <xsl:value-of select="productId"/>
                        </inventoryId>

                        <itemName>
                            <xsl:value-of select="productName"/>
                        </itemName>

                        <inventoryCategory>
                            <xsl:value-of select="category"/>
                        </inventoryCategory>

                        <availableStock>
                            <xsl:value-of select="quantity"/>
                        </availableStock>

                        <unitPrice>
                            <xsl:value-of select="unitPrice"/>
                        </unitPrice>

                        <!-- Calculated Field -->
                        <inventoryValue>
                            <xsl:value-of select="quantity * unitPrice"/>
                        </inventoryValue>

                        <!-- Business Rule -->
                        <stockStatus>

                            <xsl:choose>

                                <xsl:when test="quantity &gt; 0">
                                    AVAILABLE
                                </xsl:when>

                                <xsl:otherwise>
                                    OUT_OF_STOCK
                                </xsl:otherwise>

                            </xsl:choose>

                        </stockStatus>

                        <!-- Enriched Field -->
                        <warehouseLocation>
                            <xsl:value-of select="/ObjectNode/location"/>
                        </warehouseLocation>

                    </inventoryItem>

                </xsl:for-each>

            </inventoryItems>

        </inventoryUpdate>

    </xsl:template>

</xsl:stylesheet>
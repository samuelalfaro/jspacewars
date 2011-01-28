<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE xsl:stylesheet [<!ENTITY style SYSTEM 'shared/svg_styles.css'>]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:cc="http://creativecommons.org/ns#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:param name="scale">1.0</xsl:param>
	<xsl:param name="background">none</xsl:param>
	<xsl:output method="xml" indent="no"/>
	<xsl:preserve-space elements="*"/>

	<xsl:template match="Class|Interface|Enum">
		<xsl:call-template name="calculate_xOffsetEnclosingClassesR">
			<xsl:with-param name="count"  select="0"/>
			<xsl:with-param name="xOffsetEnclosingClasses" select="0"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="calculate_xOffsetEnclosingClassesR">
		<xsl:param name="count"/>
		<xsl:param name="xOffsetEnclosingClasses"/>
		<xsl:choose>
			<xsl:when test="EnclosingClasses">
				<xsl:choose>
					<xsl:when test="$count &lt; count(EnclosingClasses/*)">
						<xsl:variable name="nodeName" select="name(EnclosingClasses/*[$count+1])"/>
						<xsl:variable name="widthBox1" select="string-length(EnclosingClasses/*[$count+1]/text())*8+20"/>
						<xsl:variable name="widthBox">
							<xsl:choose>
								<xsl:when test="$nodeName='Class'">
									<xsl:choose>
										<xsl:when test="$widthBox1 &gt; 80"><xsl:number value="$widthBox1"/></xsl:when>
										<xsl:otherwise>80</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="$nodeName='Interface'">
									<xsl:choose>
										<xsl:when test="$widthBox1 &gt; 120"><xsl:number value="$widthBox1"/></xsl:when>
										<xsl:otherwise>120</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="$widthBox1 &gt; 140"><xsl:number value="$widthBox1"/></xsl:when>
										<xsl:otherwise>140</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:call-template name="calculate_xOffsetEnclosingClassesR">
							<xsl:with-param name="count"  select="$count +1"/>
							<xsl:with-param name="xOffsetEnclosingClasses" select="$xOffsetEnclosingClasses + $widthBox"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="GenerateSVG">
							<xsl:with-param name="xOffsetEnclosingClasses" select="$xOffsetEnclosingClasses + count(EnclosingClasses/*) * 40"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="GenerateSVG">
					<xsl:with-param name="xOffsetEnclosingClasses" select="0"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GenerateSVG">
		<xsl:param name="xOffsetEnclosingClasses"/>
		<xsl:variable name="stylesheet"><![CDATA[<?xml-stylesheet type="text/css" href="shared/svg_styles.css"?>]]></xsl:variable>
		<xsl:variable name="xOffsetHierarchy">
			<xsl:choose>
				<xsl:when test="Hierarchy">
					<xsl:choose>
						<xsl:when test="Hierarchy/Class[1]/text()='...'"><xsl:number value="( count(Hierarchy/Class) -1 )*40 -10"/></xsl:when>
						<xsl:otherwise><xsl:number value="( count(Hierarchy/Class) -1 )*40"/></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthHierarchy">
			<xsl:choose>
				<xsl:when test="Hierarchy">
					<xsl:for-each select="Hierarchy/Class">
						<xsl:sort select="string-length(.)" order="descending" data-type="number"/>
						<xsl:if test="position() = 1">
							<xsl:number value="string-length(.) * 8 + 20"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightHierarchy">
			<xsl:choose>
				<xsl:when test="Hierarchy">
					<xsl:choose>
						<xsl:when test="Hierarchy/Class[1]/text()='...'"><xsl:number value="count(Hierarchy/Class)*50 +10"/></xsl:when>
						<xsl:otherwise><xsl:number value="count(Hierarchy/Class)*50 + 35"/></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="widthName" select="string-length(/Signature) * 8 + 20"/>
		<xsl:variable name="numElements" select="count(Constant) + count(Fields/Field) + count(Constructors/Constructor) + count(Methods/Method)"/>
		<xsl:variable name="widthElements">
			<xsl:choose>
				<xsl:when test="$numElements &gt; 0">
					<xsl:for-each select="Fields/Field|Constructors/Constructor|Methods/Method">
						<xsl:sort select="string-length(./Signature)" order="descending" data-type="number"/>
						<xsl:if test="position() = 1">
							<xsl:number value="string-length(./Signature) * 7 + 40"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthParameters" select="string-length(Parameters/Signature) * 8 +20"/>
		<xsl:variable name="widthBox">
			<xsl:choose>
				<xsl:when test="$widthName &gt; $widthElements and $widthName &gt; $widthParameters and $widthName &gt; 140">
					<xsl:number value="$widthName"/>
				</xsl:when>
				<xsl:when test="$widthElements &gt; $widthParameters and $widthElements &gt; 140">
					<xsl:number value="$widthElements"/>
				</xsl:when>
				<xsl:when test="$widthParameters &gt; 140">
					<xsl:number value="$widthParameters +20"/>
				</xsl:when>
				<xsl:otherwise>140</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthInterfaces">
			<xsl:choose>
				<xsl:when test="Interfaces">
					<xsl:for-each select="Interfaces/Interface">
						<xsl:sort select="string-length(.)" order="descending" data-type="number"/>
						<xsl:if test="position() = 1">
							<xsl:number value="string-length(.) * 7 + 60"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="xRemainderAux">
			<xsl:choose>
				<xsl:when test="Interfaces"><xsl:number value="$widthInterfaces"/></xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="Parameters"><xsl:number value="20"/></xsl:when>
						<xsl:otherwise>0</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="xRemainder">
			<xsl:choose>
				<xsl:when test="$widthHierarchy - $widthBox  &gt; $xRemainderAux"><xsl:number value="$widthHierarchy - $widthBox"/></xsl:when>
				<xsl:otherwise><xsl:number value="$xRemainderAux"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="xOffset">
			<xsl:choose>
				<xsl:when test="$xOffsetHierarchy  &gt; $xOffsetEnclosingClasses"><xsl:number value="$xOffsetHierarchy"/></xsl:when>
				<xsl:otherwise><xsl:number value="$xOffsetEnclosingClasses"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="widthArea" select="10 + $xOffset + $widthBox + $xRemainder + 10"/>

		<xsl:variable name="height">
			<xsl:choose>
				<xsl:when test="$numElements &gt; 0">
					<xsl:number value="($numElements + 1) * 20 + 5"/>
				</xsl:when>
				<xsl:otherwise>20</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="heightBox">
			<xsl:choose>
				<xsl:when test="name(.)='Enum'"><xsl:number value="$height + 10"/></xsl:when>
				<xsl:otherwise><xsl:number value="$height"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightInterfaces" select="count(Interfaces/Interface)*30 - 25"/>
		<xsl:variable name="heightMax">
			<xsl:choose>
				<xsl:when test="$heightBox &gt; $heightInterfaces"><xsl:number value="$heightBox"/></xsl:when>
				<xsl:otherwise><xsl:number value="$heightInterfaces"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="yLine">
			<xsl:choose>
				<xsl:when test="name(.)='Class'">
					<xsl:number value="( count(Fields/Field) )*20 + 33"/>
				</xsl:when>
				<xsl:when test="name(.)='Interface'">
					<xsl:number value="( count(Fields/Field) )*20 + 53"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="( count(Constant) + count(Fields/Field) )*20 + 63"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="estilo">
			<xsl:choose>
				<xsl:when test="name(.)='Class'">estilo1</xsl:when>
				<xsl:when test="name(.)='Interface'">estilo2</xsl:when>
				<xsl:otherwise>estilo3</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test="Hierarchy"><xsl:number value="$heightHierarchy"/></xsl:when>
				<xsl:when test="Parameters">30</xsl:when>
				<xsl:when test="name(.)='Class' and (EnclosingClasses/Interface|EnclosingClasses/Enum)">30</xsl:when>
				<xsl:otherwise>10</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="prefixYOffset">
			<xsl:choose>
				<xsl:when test="name(.)='Class'">0</xsl:when>
				<xsl:otherwise>20</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="heightArea" select="10 + $yOffset + $prefixYOffset + $heightMax + 20"/>

		<xsl:processing-instruction name="xml-stylesheet">type="text/css" href="shared/svg_styles.css"</xsl:processing-instruction>
		<xsl:element name="svg" xml:space="default">
			<xsl:attribute name="width"><xsl:number value="$widthArea * $scale"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:number value="$heightArea * $scale"/></xsl:attribute>
			<xsl:attribute name="viewBox"><xsl:value-of select="concat('0 0 ', $widthArea, ' ', $heightArea)"/></xsl:attribute>
			<xsl:attribute name="version">1.1</xsl:attribute>
			<defs>
				<path id="CirculoInteface" class="default" d="M -10,0 A 10,10 0 0,1 10,0 A 10,10 0 0,1 -10,0 z"/>
				<path id="TrianguloJeraquia" class="default" d="M -10,10 0,-10 10,10 Z"/>
				<g id="CirculoContenedor">
					<path class="default" d="M -10,0 A 10,10 0 0,1 10,0 A 10,10 0 0,1 -10,0 z"/>
					<line x1="-10" y1="0" x2="10" y2="0"/>
					<line x1="0" y1="-10" x2="0" y2="10"/>
				</g>
				<path id="CirculoInteface" d="M -10,0 A 10,10 0 0,1 10,0 A 10,10 0 0,1 -10,0 z"/>
			</defs>

			<!-- Fondo -->
			<xsl:if test="$background !='none'">
				<xsl:element name="rect" xml:space="default">
					<xsl:attribute name="x">0</xsl:attribute>
					<xsl:attribute name="y">0</xsl:attribute>
					<xsl:attribute name="width"><xsl:value-of select="$widthArea"/></xsl:attribute>
					<xsl:attribute name="height"><xsl:value-of select="$heightArea"/></xsl:attribute>
					<xsl:attribute name="style"><xsl:value-of select="concat('fill:', $background, '; stroke:none')"/></xsl:attribute>
				</xsl:element>
			</xsl:if>

			<xsl:if test="Hierarchy">
				<xsl:call-template name="drawHierarchy">
					<xsl:with-param name="xOffset">
						<xsl:choose>
							<xsl:when test="$xOffsetHierarchy &lt; $xOffsetEnclosingClasses"><xsl:number value="$xOffsetEnclosingClasses - $xOffsetHierarchy "/></xsl:when>
							<xsl:otherwise>0</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="widthBoxes" select="$widthHierarchy"/>
				</xsl:call-template>
			</xsl:if>

			<xsl:if test="EnclosingClasses">
				<xsl:call-template name="drawEnclosingClassesR">
					<xsl:with-param name="count"  select="0"/>
					<xsl:with-param name="xOffsetEnclosingClasses" select="10"/>
					<xsl:with-param name="yOffset"  select="$yOffset"/>
					<xsl:with-param name="childYOffset" select="$prefixYOffset"/>
				</xsl:call-template>
			</xsl:if>

			<xsl:element name="g" xml:space="default">
				<xsl:attribute name="transform">translate(<xsl:number value="10 + $xOffset"/>, <xsl:number value="$yOffset"/>)</xsl:attribute>              

				<xsl:call-template name="drawInterfaceLines">
					<xsl:with-param name="xOffset" select="$widthBox"/>
				</xsl:call-template>

				<g filter="url(shared/defs.svg#SpecShadow1)">
					<xsl:call-template name="drawBox">
						<xsl:with-param name="width" select="$widthBox"/>
						<xsl:with-param name="height" select="$heightBox"/>
						<xsl:with-param name="heightTitle" select="20 +$prefixYOffset"/>
						<xsl:with-param name="yLine1" select="$yLine"/>
						<xsl:with-param name="yLine2">
							<xsl:choose>
								<xsl:when test="name(.)='Enum'"><xsl:value-of select="count(Constant)*20 + 53"/></xsl:when>
								<xsl:otherwise>0</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="estilo" select="$estilo"/>
					</xsl:call-template>
				</g>

				<xsl:if test="Parameters">
					<g filter="url(shared/defs.svg#SpecShadow2)" >
						<xsl:element name="rect" xml:space="default">
							<xsl:attribute name="class"><xsl:value-of select="$estilo"/></xsl:attribute>
							<xsl:attribute name="id">parameters</xsl:attribute>
							<xsl:attribute name="x"><xsl:number value="20 + $widthBox - $widthParameters"/></xsl:attribute>
							<xsl:attribute name="y">-20</xsl:attribute>
							<xsl:attribute name="width"><xsl:number value="$widthParameters"/></xsl:attribute>
							<xsl:attribute name="height">26</xsl:attribute>
						</xsl:element>
					</g>
					<xsl:apply-templates select="Parameters">
						<xsl:with-param name="center"><xsl:number value="20 + $widthBox -( $widthParameters div 2 )"/></xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>

				<xsl:call-template name="drawTitle">
					<xsl:with-param name="center" select="$widthBox div 2"/>
					<xsl:with-param name="prefix">
						<xsl:choose>
							<xsl:when test="name(.)='Interface'"><![CDATA[<< interface >>]]></xsl:when>
							<xsl:when test="name(.)='Enum'"><![CDATA[<< enumeration >>]]></xsl:when>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="title" select="Signature/text()"/>
					<xsl:with-param name="isAbstract" select="@isAbstract"/>
				</xsl:call-template>

				<xsl:element name="g" xml:space="default">
					<xsl:attribute name="transform"><xsl:value-of select="concat('translate(7, ', 10 + $prefixYOffset, ')')"/></xsl:attribute>
					<xsl:if test="name(.)='Enum'">
						<xsl:apply-templates select="Constant"/>
					</xsl:if>
					<xsl:apply-templates select="Fields|Constructors|Methods"/>
				</xsl:element>

				<xsl:call-template name="drawInterfaces">
					<xsl:with-param name="xOffset" select="$widthBox + 40"/>
				</xsl:call-template>
				
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="drawBox">
		<xsl:param name="width"/>
		<xsl:param name="height"/>
		<xsl:param name="heightTitle"/>
		<xsl:param name="yLine1"/>
		<xsl:param name="yLine2"/>
		<xsl:param name="estilo"/>
		<xsl:element name="rect" xml:space="default">
			<xsl:attribute name="class"><xsl:value-of select="$estilo"/></xsl:attribute>
			<xsl:attribute name="id">contenido</xsl:attribute>
			<xsl:attribute name="x">0</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value="$heightTitle"/></xsl:attribute>
			<xsl:attribute name="width"><xsl:number value="$width"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:number value="$height"/></xsl:attribute>
			<xsl:attribute name="rx">2</xsl:attribute>
		</xsl:element>
		<xsl:element name="rect" xml:space="default">
			<xsl:attribute name="class"><xsl:value-of select="$estilo"/></xsl:attribute>
			<xsl:attribute name="id">titulo</xsl:attribute>
			<xsl:attribute name="x">0</xsl:attribute>
			<xsl:attribute name="y">0</xsl:attribute>
			<xsl:attribute name="width"><xsl:number value="$width"/></xsl:attribute>
			<xsl:attribute name="height"><xsl:number value="$heightTitle + 6"/></xsl:attribute>
			<xsl:attribute name="rx">2</xsl:attribute>
		</xsl:element>
		<xsl:element name="line" xml:space="default">
			<xsl:attribute name="x1">0</xsl:attribute>
			<xsl:attribute name="y1"><xsl:number value="$yLine1"/></xsl:attribute>
			<xsl:attribute name="x2"><xsl:number value="$width"/></xsl:attribute>
			<xsl:attribute name="y2"><xsl:number value="$yLine1"/></xsl:attribute>
		</xsl:element>
		<xsl:if test="$yLine2 &gt; 0">
			<xsl:element name="line" xml:space="default">
				<xsl:attribute name="x1">0</xsl:attribute>
				<xsl:attribute name="y1"><xsl:number value="$yLine2"/></xsl:attribute>
				<xsl:attribute name="x2"><xsl:number value="$width"/></xsl:attribute>
				<xsl:attribute name="y2"><xsl:number value="$yLine2"/></xsl:attribute>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template name="drawTitle">
		<xsl:param name="center"/>
		<xsl:param name="prefix"/>
		<xsl:param name="title"/>
		<xsl:param name="isAbstract"/>
		<xsl:choose>
			<xsl:when test="string-length($prefix) &gt; 0">
				<xsl:element name="text" xml:space="default">
					<xsl:attribute name="x"><xsl:number value="$center"/></xsl:attribute>
					<xsl:attribute name="y">18</xsl:attribute>
					<xsl:attribute name="class">centrado</xsl:attribute>
					<xsl:value-of select="$prefix"/>
				</xsl:element>
				<xsl:element name="text" xml:space="default">
					<xsl:attribute name="id">textoTitulo</xsl:attribute>
					<xsl:if test="$isAbstract='true'">
						<xsl:attribute name="class">abstract</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="x"><xsl:number value="$center"/></xsl:attribute>
					<xsl:attribute name="y">38</xsl:attribute>
					<xsl:value-of select="$title"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="text" xml:space="default">
					<xsl:attribute name="id">textoTitulo</xsl:attribute>
					<xsl:if test="$isAbstract='true'">
						<xsl:attribute name="class">abstract</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="x"><xsl:number value="$center"/></xsl:attribute>
					<xsl:attribute name="y">18</xsl:attribute>
					<xsl:value-of select="$title"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="drawHierarchy">
		<xsl:param name="xOffset"/>
		<xsl:param name="widthBoxes"/>
		<xsl:element name="g" xml:space="default">
			<xsl:if test="Hierarchy/Class[1]/text()='...'">
				<xsl:attribute name="transform">translate(-10, -25)</xsl:attribute>
			</xsl:if>
			<xsl:for-each select="Hierarchy/Class">
				<xsl:variable name="x" select="( position() -1 )*40 + 30 +$xOffset"/>
				<xsl:variable name="y" select=" position() *50 + 15"/>
				<xsl:choose>
					<xsl:when test="position() = last()">
						<xsl:element name="line" xml:space="default">
							<xsl:attribute name="x1"><xsl:number value="$x"/></xsl:attribute>
							<xsl:attribute name="y1"><xsl:number value="$y"/></xsl:attribute>
							<xsl:attribute name="x2"><xsl:number value="$x"/></xsl:attribute>
							<xsl:attribute name="y2"><xsl:number value="$y + 20 "/></xsl:attribute>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="line" xml:space="default">
							<xsl:attribute name="x1"><xsl:number value="$x"/></xsl:attribute>
							<xsl:attribute name="y1"><xsl:number value="$y"/></xsl:attribute>
							<xsl:attribute name="x2"><xsl:number value="$x"/></xsl:attribute>
							<xsl:attribute name="y2"><xsl:number value="$y +10"/></xsl:attribute>
						</xsl:element>
						<xsl:element name="line" xml:space="default">
							<xsl:attribute name="x1"><xsl:number value="$x"/></xsl:attribute>
							<xsl:attribute name="y1"><xsl:number value="$y +10"/></xsl:attribute>
							<xsl:attribute name="x2"><xsl:number value="$x +20"/></xsl:attribute>
							<xsl:attribute name="y2"><xsl:number value="$y +10"/></xsl:attribute>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<xsl:for-each select="Hierarchy/Class">
				<xsl:element name="use" xml:space="default">
					<xsl:attribute name="x"><xsl:number value="( position() -1 )*40 + 30 + $xOffset"/></xsl:attribute>
					<xsl:attribute name="y"><xsl:number value="position() *50 + 5"/></xsl:attribute>
					<xsl:attribute name="xlink:href">#TrianguloJeraquia</xsl:attribute>
					<xsl:attribute name="filter">url(shared/defs.svg#SpecShadow2)</xsl:attribute>
				</xsl:element>
				<xsl:element name="g" xml:space="default">
					<xsl:attribute name="transform">translate(<xsl:number value="( position() -1 )*40 + 10 + $xOffset"/>, <xsl:number value="( position() -1 )*50 +10"/>)</xsl:attribute>
					<xsl:choose>
						<xsl:when test="position() = 1 and text()='...'">
							<text id="textoTitulo" x="20" y="30">...</text>
						</xsl:when>
						<xsl:otherwise>
							<g filter="url(shared/defs.svg#SpecShadow2)">
								<xsl:call-template name="drawBox">
									<xsl:with-param name="width"  select="$widthBoxes"/>
									<xsl:with-param name="height" select="15"/>
									<xsl:with-param name="heightTitle" select="20"/>
									<xsl:with-param name="yLine1" select="30"/>
									<xsl:with-param name="yLine2" select="0"/>
									<xsl:with-param name="estilo" select="'estilo1'"/>
								</xsl:call-template>
							</g>
							<xsl:call-template name="drawTitle">
								<xsl:with-param name="center" select="$widthBoxes div 2"/>
								<xsl:with-param name="prefix" select="''"/>
								<xsl:with-param name="title" select="text()"/>
								<xsl:with-param name="isAbstract" select="@isAbstract"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template name="drawEnclosingClassesR">
		<xsl:param name="count"/>
		<xsl:param name="xOffsetEnclosingClasses"/>
		<xsl:param name="yOffset"/>
		<xsl:param name="childYOffset"/>
		<xsl:if test="$count &lt; count(EnclosingClasses/*)">
			<xsl:variable name="nodeName" select="name(EnclosingClasses/*[$count+1])"/>
			<xsl:variable name="widthBox1" select="string-length(EnclosingClasses/*[$count+1]/text())*8+20"/>
			<xsl:variable name="widthBox">
				<xsl:choose>
					<xsl:when test="$nodeName='Class'">
						<xsl:choose>
							<xsl:when test="$widthBox1 &gt; 80"><xsl:number value="$widthBox1"/></xsl:when>
							<xsl:otherwise>80</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="$nodeName='Interface'">
						<xsl:choose>
							<xsl:when test="$widthBox1 &gt; 120"><xsl:number value="$widthBox1"/></xsl:when>
							<xsl:otherwise>120</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$widthBox1 &gt; 140"><xsl:number value="$widthBox1"/></xsl:when>
							<xsl:otherwise>140</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="prefixYOffset">
				<xsl:choose>
					<xsl:when test="$nodeName='Class'">0</xsl:when>
					<xsl:otherwise>20</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:element name="g" xml:space="default">
				<xsl:attribute name="transform">translate(<xsl:number value="$xOffsetEnclosingClasses"/>, <xsl:number value="$yOffset - $prefixYOffset +$childYOffset "/>)</xsl:attribute>
				<g filter="url(shared/defs.svg#SpecShadow2)">
					<xsl:call-template name="drawBox">
						<xsl:with-param name="width"  select="$widthBox"/>
						<xsl:with-param name="height" select="15"/>
						<xsl:with-param name="heightTitle" select="20 + $prefixYOffset"/>
						<xsl:with-param name="yLine1" select="30 + $prefixYOffset"/>
						<xsl:with-param name="yLine2" select="0"/>
						<xsl:with-param name="estilo">
							<xsl:choose>
								<xsl:when test="$nodeName='Class'">estilo1</xsl:when>
								<xsl:when test="$nodeName='Interface'">estilo2</xsl:when>
								<xsl:otherwise>estilo3</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</g>
				<xsl:call-template name="drawTitle">
					<xsl:with-param name="center" select="$widthBox div 2"/>
					<xsl:with-param name="prefix">
						<xsl:choose>
							<xsl:when test="$nodeName='Interface'"><![CDATA[<< interface >>]]></xsl:when>
							<xsl:when test="$nodeName='Enum'">     <![CDATA[<< enumeration >>]]></xsl:when>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="title" select="EnclosingClasses/*[$count+1]/text()"/>
					<xsl:with-param name="isAbstract" select="EnclosingClasses/*[$count+1]/@isAbstract"/>
				</xsl:call-template>
			</xsl:element>
			<xsl:element name="line" xml:space="default">
				<xsl:attribute name="x1"><xsl:number value="$xOffsetEnclosingClasses+ $widthBox + 20"/></xsl:attribute>
				<xsl:attribute name="y1"><xsl:number value="$yOffset  +$childYOffset + 13"/></xsl:attribute>
				<xsl:attribute name="x2"><xsl:number value="$xOffsetEnclosingClasses+ $widthBox + 40"/></xsl:attribute>
				<xsl:attribute name="y2"><xsl:number value="$yOffset  +$childYOffset + 13"/></xsl:attribute>
			</xsl:element>
			<xsl:element name="use" xml:space="default">
				<xsl:attribute name="x"><xsl:number value="$xOffsetEnclosingClasses+ $widthBox + 10"/></xsl:attribute>
				<xsl:attribute name="y"><xsl:number value="$yOffset  +$childYOffset + 13"/></xsl:attribute>
				<xsl:attribute name="xlink:href">#CirculoContenedor</xsl:attribute>
				<xsl:attribute name="filter">url(shared/defs.svg#SpecShadow2)</xsl:attribute>
			</xsl:element>
			<xsl:call-template name="drawEnclosingClassesR">
				<xsl:with-param name="count"  select="$count +1"/>
				<xsl:with-param name="xOffsetEnclosingClasses" select="$xOffsetEnclosingClasses + $widthBox +40"/>
				<xsl:with-param name="yOffset"  select="$yOffset"/>
				<xsl:with-param name="childYOffset"  select="$childYOffset"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="drawInterfaceLines">
		<xsl:param name="xOffset"/>
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test="name(.)='Class'">14</xsl:when>
				<xsl:otherwise>34</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="count(Interfaces/Interface) &gt; 1">
			<xsl:element name="line" xml:space="default">
				<xsl:attribute name="x1"><xsl:number value="$xOffset + 15"/></xsl:attribute>
				<xsl:attribute name="y1"><xsl:number value="$yOffset"/></xsl:attribute>
				<xsl:attribute name="x2"><xsl:number value="$xOffset + 15"/></xsl:attribute>
				<xsl:attribute name="y2"><xsl:number value="count(Interfaces/Interface) * 30 + $yOffset - 40"/></xsl:attribute>
			</xsl:element>
		</xsl:if>
		<xsl:for-each select="Interfaces/Interface">
			<xsl:variable name="y" select="( position() -1 )*30 + $yOffset"/>
			<xsl:choose>
				<xsl:when test="position() = 1">
					<xsl:element name="line" xml:space="default">
						<xsl:attribute name="x1"><xsl:number value="$xOffset"/></xsl:attribute>
						<xsl:attribute name="y1"><xsl:number value="$y"/></xsl:attribute>
						<xsl:attribute name="x2"><xsl:number value="$xOffset + 31"/></xsl:attribute>
						<xsl:attribute name="y2"><xsl:number value="$y"/></xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="line" xml:space="default">
						<xsl:attribute name="x1"><xsl:number value="$xOffset + 15"/></xsl:attribute>
						<xsl:attribute name="y1"><xsl:number value="$y - 10"/></xsl:attribute>
						<xsl:attribute name="x2"><xsl:number value="$xOffset + 25"/></xsl:attribute>
						<xsl:attribute name="y2"><xsl:number value="$y"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="line" xml:space="default">
						<xsl:attribute name="x1"><xsl:number value="$xOffset + 25"/></xsl:attribute>
						<xsl:attribute name="y1"><xsl:number value="$y"/></xsl:attribute>
						<xsl:attribute name="x2"><xsl:number value="$xOffset + 31"/></xsl:attribute>
						<xsl:attribute name="y2"><xsl:number value="$y"/></xsl:attribute>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="drawInterfaces">
		<xsl:param name="xOffset"/>
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test="name(.)='Class'">14</xsl:when>
				<xsl:otherwise>34</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="Interfaces/Interface">
			<xsl:element name="use" xml:space="default">
				<xsl:attribute name="x"><xsl:number value="$xOffset"/></xsl:attribute>
				<xsl:attribute name="y"><xsl:number value="( position() -1 )*30 + $yOffset"/></xsl:attribute>
				<xsl:attribute name="xlink:href">#CirculoInteface</xsl:attribute>
				<xsl:attribute name="filter">url(shared/defs.svg#SpecShadow2)</xsl:attribute>
			</xsl:element>
			<xsl:element name="text" xml:space="default">
				<xsl:attribute name="x"><xsl:number value="$xOffset + 15"/></xsl:attribute>
				<xsl:attribute name="y"><xsl:number value="( position() -1 )*30 + $yOffset + 4"/></xsl:attribute>
				<xsl:value-of select="text()"/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="Parameters">
		<xsl:param name="center"/>
		<xsl:element name="text" xml:space="default">
			<xsl:attribute name="x"><xsl:value-of select="$center"/></xsl:attribute>
			<xsl:attribute name="y">-2</xsl:attribute>
			<xsl:attribute name="id">textoTitulo</xsl:attribute>
			<xsl:value-of select="Signature/text()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Fields">
		<xsl:apply-templates select="Field"/>
	</xsl:template>

	<xsl:template match="Constructors">
		<xsl:apply-templates select="Constructor"/>
	</xsl:template>

	<xsl:template match="Methods">
		<xsl:apply-templates select="Method"/>
	</xsl:template>

	<xsl:template match="Constant">
		<xsl:element name="use" xml:space="default">
			<xsl:attribute name="x">0</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value="position()*20"/></xsl:attribute>
			<xsl:attribute name="xlink:href">shared/defs.svg#StaticPublicField</xsl:attribute>
		</xsl:element>
		<xsl:element name="text" xml:space="default">
			<xsl:attribute name="class">static</xsl:attribute>
			<xsl:attribute name="x">23</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value="position()*20 +13"/></xsl:attribute>
			<xsl:value-of select="Signature/text()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Field">
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test="name(../..)='Enum'">
					<xsl:number value="( count(../../Constant) )*20 + 10"/>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="use" xml:space="default">
			<xsl:attribute name="x">0</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value="position()*20 + $yOffset"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="@visibility='+'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticPublicField</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#PublicField</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="@visibility='~'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticPackageField</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#PackageField</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="@visibility='#'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticProtectedField</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#ProtectedField</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="@visibility='-'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticPrivateField</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#PrivateField</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
		<xsl:element name="text" xml:space="default">
			<xsl:choose>
				<xsl:when test="@isStatic='true'">
				  <xsl:attribute name="class">static</xsl:attribute>
				</xsl:when>
				<xsl:when test="@isAbstract='true'">
				  <xsl:attribute name="class">abstract</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:attribute name="x">23</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value=" position() *20 + $yOffset +13"/></xsl:attribute>
			<xsl:value-of select="Signature/text()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Constructor">
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test="name(../..)='Enum'">
					<xsl:number value="( count(../../Constant) + count(../../Fields/Field) )*20 + 20"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="( count(../../Fields/Field) )*20 + 10"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="use" xml:space="default">
			<xsl:attribute name="x">0</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value=" position() *20 + $yOffset"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="@visibility='+'">
					<xsl:attribute name="xlink:href">shared/defs.svg#PublicConstructor</xsl:attribute>
				</xsl:when>
				<xsl:when test="@visibility='~'">
					<xsl:attribute name="xlink:href">shared/defs.svg#PackageConstructor</xsl:attribute>
				</xsl:when>
				<xsl:when test="@visibility='#'">
					<xsl:attribute name="xlink:href">shared/defs.svg#ProtectedConstructor</xsl:attribute>
				</xsl:when>
				<xsl:when test="@visibility='-'">
					<xsl:attribute name="xlink:href">shared/defs.svg#PrivateConstructor</xsl:attribute>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
		<xsl:element name="text" xml:space="default">
			<xsl:choose>
				<xsl:when test="@isStatic='true'">
				  <xsl:attribute name="class">static</xsl:attribute>
				</xsl:when>
				<xsl:when test="@isAbstract='true'">
				  <xsl:attribute name="class">abstract</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:attribute name="x">23</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value=" position() *20 + $yOffset +13"/></xsl:attribute>
			<xsl:value-of select="Signature/text()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Method">
		<xsl:variable name="yOffset">
			<xsl:choose>
				<xsl:when test="name(../..)='Enum'">
					<xsl:number value="( count(../../Constant) + count(../../Fields/Field) + count(../../Constructors/Constructor) )*20 + 20"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="( count(../../Fields/Field) + count(../../Constructors/Constructor) )*20 + 10"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="use" xml:space="default">
			<xsl:attribute name="x">0</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value=" position() *20 + $yOffset"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="@visibility='+'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticPublicMethod</xsl:attribute>
						</xsl:when>
						<xsl:when test="@isAbstract='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#AbstractPublicMethod</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#PublicMethod</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="@visibility='~'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticPackageMethod</xsl:attribute>
						</xsl:when>
						<xsl:when test="@isAbstract='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#AbstractPackageMethod</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#PackageMethod</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="@visibility='#'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticProtectedMethod</xsl:attribute>
						</xsl:when>
						<xsl:when test="@isAbstract='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#AbstractProtectedMethod</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#ProtectedMethod</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="@visibility='-'">
					<xsl:choose>
						<xsl:when test="@isStatic='true'">
							<xsl:attribute name="xlink:href">shared/defs.svg#StaticPrivateMethod</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="xlink:href">shared/defs.svg#PrivateMethod</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
		<xsl:element name="text" xml:space="default">
			<xsl:choose>
				<xsl:when test="@isStatic='true'">
				  <xsl:attribute name="class">static</xsl:attribute>
				</xsl:when>
				<xsl:when test="@isAbstract='true'">
				  <xsl:attribute name="class">abstract</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:attribute name="x">23</xsl:attribute>
			<xsl:attribute name="y"><xsl:number value="position() *20 + $yOffset +13"/></xsl:attribute>
			<xsl:value-of select="Signature/text()"/>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
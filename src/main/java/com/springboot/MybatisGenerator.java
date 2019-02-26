package com.springboot;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Objects;

public class MybatisGenerator extends PluginAdapter {

	public static void main(String[] args) {
		generate();
	}

	public static void generate() {
		String config = Objects.requireNonNull(MybatisGenerator.class.getClassLoader().getResource("generator/mybatis-generator.xml")).getFile();
		String[] arg = { "-configfile", config, "-overwrite" };
		ShellRunner.main(arg);
	}

	public boolean validate(List<String> list) {
		return true;
	}

	/**
	 * 实体类添加swagger注解
	 * @param field
	 * @param topLevelClass
	 * @param introspectedColumn
	 * @param introspectedTable
	 * @param modelClassType
	 * @return
	 */
	public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType) {
		String classAnnotation = "@ApiModel(value=\"" + topLevelClass.getType().getShortName() + "\")";
		if (!topLevelClass.getAnnotations().contains(classAnnotation)) {
			topLevelClass.addAnnotation(classAnnotation);
		}
		String apiModelAnnotationPackage = this.properties.getProperty("apiModelAnnotationPackage");
		String apiModelPropertyAnnotationPackage = this.properties.getProperty("apiModelPropertyAnnotationPackage");
		if (null == apiModelAnnotationPackage) {
			apiModelAnnotationPackage = "io.swagger.annotations.ApiModel";
		}
		if (null == apiModelPropertyAnnotationPackage) {
			apiModelPropertyAnnotationPackage = "io.swagger.annotations.ApiModelProperty";
		}
		topLevelClass.addImportedType(apiModelAnnotationPackage);
		topLevelClass.addImportedType(apiModelPropertyAnnotationPackage);
		field.addAnnotation("@ApiModelProperty(value=\"" + introspectedColumn.getRemarks() +
										   "\",name=\""+introspectedColumn.getJavaProperty()+
										   "\",dataType=\""+introspectedColumn.getFullyQualifiedJavaType().getShortName()+
											"\")");
		return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
	}

	/**
	 * 生成dao
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseDao<" + introspectedTable.getBaseRecordType() + ","+introspectedTable.getBaseRecordType()+"Example>");
		FullyQualifiedJavaType imp = new FullyQualifiedJavaType("com.springboot.dao.base.BaseDao");
		interfaze.addSuperInterface(fqjt);// 添加 extends BaseDao<User>
		interfaze.addImportedType(imp);// 添加import common.BaseDao;
		interfaze.getMethods().clear();
		return true;
	}

	/**
	 * 生成实体中每个属性
	 *//*
	@Override
	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
											  IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return true;
	}

	*//**
	 * 生成实体
	 *//*
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		addSerialVersionUID(topLevelClass, introspectedTable);
		return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
	}

	*//**
	 * 生成mapping
	 *//*
	@Override
	public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
		return super.sqlMapGenerated(sqlMap, introspectedTable);
	}

	*//**
	 * 生成mapping 添加自定义sql
	 *//*
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();// 数据库表名
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		XmlElement parentElement = document.getRootElement();

		// 添加sql——where
		XmlElement sql = new XmlElement("sql");
		sql.addAttribute(new Attribute("id", "sql_where"));
		XmlElement where = new XmlElement("where");
		StringBuilder sb = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
			XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sb.setLength(0);
			sb.append(introspectedColumn.getJavaProperty());
			sb.append(" != null"); //$NON-NLS-1$
			isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
			where.addElement(isNotNullElement);

			sb.setLength(0);
			sb.append(" and ");
			sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
			sb.append(" = "); //$NON-NLS-1$
			sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
			isNotNullElement.addElement(new TextElement(sb.toString()));
		}
		sql.addElement(where);
		parentElement.addElement(sql);

		//添加getList
		XmlElement select = new XmlElement("select");
		select.addAttribute(new Attribute("id", "getList"));
		select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
		select.addElement(new TextElement(" select * from "+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));

		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "sql_where"));

		select.addElement(include);
		parentElement.addElement(select);

		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	@Override
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
																		IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
																	 IntrospectedTable introspectedTable) {
		// LIMIT5,10; // 检索记录行 6-15
		//		XmlElement isNotNullElement = new XmlElement("if");//$NON-NLS-1$
		//		isNotNullElement.addAttribute(new Attribute("test", "limitStart != null and limitStart >=0"));//$NON-NLS-1$ //$NON-NLS-2$
		// isNotNullElement.addElement(new
		// TextElement("limit ${limitStart} , ${limitEnd}"));
		// element.addElement(isNotNullElement);
		// LIMIT 5;//检索前 5个记录行
		return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
	}

	*//**
	 * mapping中添加方法
	 *//*
	// @Override
	public boolean sqlMapDocumentGenerated2(Document document, IntrospectedTable introspectedTable) {
		String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();// 数据库表名
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
		// 添加sql
		XmlElement sql = new XmlElement("select");

		XmlElement parentElement = document.getRootElement();
		XmlElement deleteLogicByIdsElement = new XmlElement("update");
		deleteLogicByIdsElement.addAttribute(new Attribute("id", "deleteLogicByIds"));
		deleteLogicByIdsElement
				.addElement(new TextElement(
						"update "
								+ tableName
								+ " set deleteFlag = #{deleteFlag,jdbcType=INTEGER} where id in "
								+ " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach> "));

		parentElement.addElement(deleteLogicByIdsElement);
		XmlElement queryPage = new XmlElement("select");
		queryPage.addAttribute(new Attribute("id", "queryPage"));
		queryPage.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		queryPage.addElement(new TextElement("select "));

		XmlElement include = new XmlElement("include");
		include.addAttribute(new Attribute("refid", "Base_Column_List"));

		queryPage.addElement(include);
		queryPage.addElement(new TextElement(" from " + tableName + " ${sql}"));
		parentElement.addElement(queryPage);
		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	private void addSerialVersionUID(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(new FullyQualifiedJavaType("long"));
		field.setStatic(true);
		field.setFinal(true);
		field.setName("serialVersionUID");
		field.setInitializationString("1L");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
	}

	*//*
	 * Dao中添加方法
	 *//*
	private Method generateDeleteLogicByIds(Method method, IntrospectedTable introspectedTable) {
		Method m = new Method("deleteLogicByIds");
		m.setVisibility(method.getVisibility());
		m.setReturnType(FullyQualifiedJavaType.getIntInstance());
		m.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "deleteFlag", "@Param(\"deleteFlag\")"));
		m.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids", "@Param(\"ids\")"));
		context.getCommentGenerator().addGeneralMethodComment(m, introspectedTable);
		return m;
	}

	*//*
	 * 实体中添加属性
	 *//*
	private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getIntInstance());
		field.setName(name);
		field.setInitializationString("-1");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		char c = name.charAt(0);
		String camel = Character.toUpperCase(c) + name.substring(1);
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("set" + camel);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
		method.addBodyLine("this." + name + "=" + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.setName("get" + camel);
		method.addBodyLine("return " + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
	}*/

}
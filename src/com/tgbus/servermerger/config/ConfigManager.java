package com.tgbus.servermerger.config;

import com.tgbus.servermerger.datacache.CacheMeta;
import com.tgbus.servermerger.datacache.Column;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private Config config = null;
    private String classpathconfig = null;
    private String absolutepathconfig = null;

    public void init() {
        try {
            //config=new Config();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc;
            if (StringUtils.isNotEmpty(classpathconfig)) {
                doc = builder.parse(this.getClass().getClassLoader().getResourceAsStream(classpathconfig));
            } else if (StringUtils.isNotEmpty(absolutepathconfig)) {
                doc = builder.parse(new FileInputStream(absolutepathconfig));
            } else {
                doc = builder.parse(this.getClass().getClassLoader().getResourceAsStream("config.xml"));
            }
            Element rootElement = doc.getDocumentElement();
            java.util.List tasks = new ArrayList<Task>();
            NodeList tasksList = rootElement.getElementsByTagName("task");
            for (int i = 0; i < tasksList.getLength(); i++) {
                String type = ((Element) tasksList.item(i)).getAttribute("type");
                String side = ((Element) tasksList.item(i)).getAttribute("side");
                String name = ((Element) tasksList.item(i)).getAttribute("name");
                String text = ((Element) tasksList.item(i)).getTextContent();

                Task task = new Task(type, name, side, text);
                tasks.add(task);
            }


            NodeList dbList = rootElement.getElementsByTagName("dbconfig");
            Element dbMsg = (Element) dbList.item(0);
            String srcURL = dbMsg.getAttribute("srcurl");
            String destURL = dbMsg.getAttribute("desturl");
            String srcUser = dbMsg.getAttribute("srcuser");
            String destUser = dbMsg.getAttribute("destuser");
            String srcPass = dbMsg.getAttribute("srcpass");
            String destPass = dbMsg.getAttribute("destpass");
            String driverName = dbMsg.getAttribute("driverName");


            Element modulesElement = (Element) rootElement.getElementsByTagName("modules").item(0);
            NodeList moduleList = modulesElement.getElementsByTagName("module");
            Set<Module> modules = new HashSet<Module>();
            for (int i = 0; i < moduleList.getLength(); i++) {
                Module module = new Module();
                Element moduleElement = (Element) moduleList.item(i);
                String id = moduleElement.getAttribute("id");
                module.setId(id);
                java.util.List tableNames = new ArrayList<String>();
                NodeList tableList = moduleElement.getElementsByTagName("include");
                for (int j = 0; j < tableList.getLength(); j++) {
                    Element table = (Element) tableList.item(j);
                    String tableName = table.getAttribute("name");
                    tableNames.add(tableName);
                }
                module.setTableNames(tableNames);
                modules.add(module);
            }
            //config.setModules(modules);

/*            ArrayList<String> moduleSequence = new ArrayList<String>();
            Element module_seq = (Element) rootElement.getElementsByTagName(
                    "module_seq").item(0);
            NodeList nodelist = module_seq.getElementsByTagName("module");
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element e = (Element) nodelist.item(i);
                String moduleId = e.getAttribute("id").trim();
                moduleSequence.add(moduleId);

            }*/
            //config.setModuleSequence(moduleSequence);

            java.util.List importModules = new ArrayList<String>();
            Element element = (Element) rootElement.getElementsByTagName(
                    "import_module").item(0);
            NodeList modele = element.getElementsByTagName("module");
            for (int i = 0; i < modele.getLength(); i++) {
                Element e = (Element) modele.item(i);
                String id = e.getAttribute("id").trim();
                importModules.add(id);
            }
            //config.setImportModules(importModules);


            Element tablesElement = (Element) rootElement.getElementsByTagName("tables").item(0);
            NodeList tableList = tablesElement.getElementsByTagName("table");
            Set<Table> tables = new HashSet<Table>();

            for (int i = 0; i < tableList.getLength(); i++) {
                Table table = new Table();
                Element tableElement = (Element) tableList.item(i);
                String name = tableElement.getAttribute("name");
                String mergerImpl = tableElement.getAttribute("impl");
                String condition = tableElement.getAttribute("condition");
                String ignorefail = tableElement.getAttribute("ignorefail");
                table.setCondition(condition);
                table.setName(name);
                table.setMergerImpl(mergerImpl);
                table.setIgnorefail(StringUtils.equalsIgnoreCase(ignorefail, "true"));
                NodeList metaList = null;
                if (tableElement.getElementsByTagName("Cache_write").getLength() != 0) {
                    metaList = ((Element) tableElement.getElementsByTagName("Cache_write").
                            item(0)).
                            getElementsByTagName("metaid");
                }

                if (metaList != null) {
                    Set<String> metas = new HashSet<String>();
                    for (int j = 0; j < metaList.getLength(); j++) {
                        metas.add(((Element) metaList.item(j)).getAttribute("id"));
                    }
                    table.setMetas(metas);
                }


                Element pkElement = (Element) tableElement.getElementsByTagName("pk")
                        .item(0);
                if (pkElement != null) {
                    PrimaryKey primaryKey = new PrimaryKey();
                    primaryKey.setUnset(pkElement.getAttribute("unset").equalsIgnoreCase("true") ? true : false);
                    List<String> columns = new ArrayList<String>();
                    primaryKey.setColumns(columns);


                    NodeList columnList = pkElement.getElementsByTagName("column");
                    for (int j = 0; j < columnList.getLength(); j++) {
                        Element columnElement = (Element) columnList.item(j);
                        columns.add(columnElement.getAttribute("name"));

                    }

                    table.setPk(primaryKey);
                }


                Element replaceElement = (Element) tableElement.getElementsByTagName("replace").item(0);
                if (replaceElement != null) {
                    HashSet<Replacement> replacements = new HashSet<Replacement>();
                    NodeList replacecolumnList = replaceElement.getElementsByTagName("replacecolumn");
                    for (int j = 0; j < replacecolumnList.getLength(); j++) {
                        Replacement replacement = new Replacement();
                        Element replacecolumnElement = (Element) replacecolumnList.item(j);
                        replacement.setSearchMetaId(replacecolumnElement.getAttribute("searchMetaId"));
                        replacement.setRewriteValue(replacecolumnElement.getAttribute("rewriteValue"));
                        replacement.setColumnName(replacecolumnElement.getAttribute("colname"));
                        replacement.setType(replacecolumnElement.getAttribute("type"));
                        replacement.setCanMiss(replacecolumnElement.getAttribute("canMiss").equalsIgnoreCase("true") ? true : false);
                        replacement.setMissWriteBack(replacecolumnElement.getAttribute("missWriteBack").equalsIgnoreCase("true") ? true : false);
                        replacement.setPk(replacecolumnElement.getAttribute("ispk").equalsIgnoreCase("true") ? true : false);
                        replacement.setDeterminter(replacecolumnElement.getAttribute("determinter"));
                        List<ReplaceCondition> replaceConditions = null;

                        for (int k = 0; k < replacecolumnElement.getElementsByTagName("condition").getLength(); k++) {
                            if (replaceConditions == null) {
                                replaceConditions = new ArrayList<ReplaceCondition>();
                            }
                            Element replaceConditionElement = (Element) replacecolumnElement.getElementsByTagName("condition").item(k);
                            ReplaceCondition replaceCondition = new ReplaceCondition();
                            replaceCondition.setCanMiss(replaceConditionElement.getAttribute("canMiss").equalsIgnoreCase("true") ? true : false);
                            replaceCondition.setColumnName(replaceConditionElement.getAttribute("colname"));
                            replaceCondition.setSearchMetaId(replaceConditionElement.getAttribute("searchMetaId"));
                            replaceCondition.setValue(replaceConditionElement.getAttribute("value"));
                            replaceConditions.add(replaceCondition);
                        }
                        replacement.setReplaceConditions(replaceConditions);

                        Map<String, String> columnAlias = new HashMap<String, String>();
                        NodeList alias = replacecolumnElement.getElementsByTagName("columnAlias");
                        if (alias != null) {
                            for (int k = 0; k < alias.getLength(); k++) {
                                Element alia = (Element) alias.item(k);
                                columnAlias.put(alia.getAttribute("colname"), alia.getAttribute("alias"));
                            }
                        }
                        replacement.setColumnAlias(columnAlias);

                        replacements.add(replacement);
                    }
                    table.setReplacements(replacements);
                    List<NoInsertTableMerge> noInsertTableMerges = null;
                    Element noInsertTableMergeElement = (Element) tableElement.getElementsByTagName("noinsertmerge").item(0);
                    if (noInsertTableMergeElement != null) {
                        noInsertTableMerges = new ArrayList<NoInsertTableMerge>();
                        for (int k = 0; k < noInsertTableMergeElement.getElementsByTagName("noinsertmergecolumn").getLength(); k++) {
                            Element noinsertmergecolumn = (Element) noInsertTableMergeElement.getElementsByTagName("noinsertmergecolumn").item(k);
                            NoInsertTableMerge noInsertTableMerge = new NoInsertTableMerge();
                            noInsertTableMerge.setColname(noinsertmergecolumn.getAttribute("name"));
                            noInsertTableMerge.setOperation(noinsertmergecolumn.getAttribute("operation"));
                            noInsertTableMerges.add(noInsertTableMerge);
                        }

                    }

                    table.setNoInsertTableMerge(noInsertTableMerges);
                }
                tables.add(table);
            }
            //config.setTables(tables);
            //cleaners ‘› ±√ª”–≈‰÷√
            Set<Cleaner> cleaners = null;
            // ≈‰÷√metas
            Set<CacheMeta> cacheMetas = null;
            NodeList metaList = ((Element) ((Element) rootElement.getElementsByTagName("cache").item(0)).getElementsByTagName("metas").item(0)).getElementsByTagName("meta");
            if (metaList.getLength() != 0) {
                cacheMetas = new HashSet<CacheMeta>();
            }

            for (int i = 0; i < metaList.getLength(); i++) {

                CacheMeta cacheMeta = new CacheMeta();
                Set<Column> coulums = new HashSet<Column>();
                cacheMetas.add(cacheMeta);
                Element metaElement = (Element) metaList.item(i);
                //cacheMeta.setColumnName(metaElement.getAttribute("columnName"));
                cacheMeta.setId(metaElement.getAttribute("id"));
                cacheMeta.setKeyColumn(metaElement.getAttribute("keyColumn"));
                cacheMeta.setTableName(metaElement.getAttribute("tableName"));
                cacheMeta.setType(metaElement.getAttribute("type"));
                String global = metaElement.getAttribute("global");
                if (StringUtils.isNotEmpty(global)) {
                    cacheMeta.setGlobal(Boolean.parseBoolean(global));
                }
                cacheMeta.setColumns(coulums);
                NodeList columnList = ((Element) metaElement.
                        getElementsByTagName("columns")
                        .item(0)).
                        getElementsByTagName("column");

                for (int j = 0; j < columnList.getLength(); j++) {
                    Column column = new Column();
                    coulums.add(column);
                    Element columnElement = (Element) columnList.item(j);
                    column.setName(columnElement.getAttribute("name"));
                    column.setOptional(columnElement.getAttribute("optional").equalsIgnoreCase("true") ? true : false);
                }

            }
            DBConfig dbConfig = new DBConfig();
            dbConfig.setDriverName(driverName);
            dbConfig.setSrcURL(srcURL);
            dbConfig.setSrcUser(srcUser);
            dbConfig.setSrcPass(srcPass);

            dbConfig.setDestURL(destURL);
            dbConfig.setDestUser(destUser);
            dbConfig.setDestPass(destPass);

            config = new Config(cleaners, dbConfig,
                    importModules,
                    modules, tables, cacheMetas, tasks);
            if (!rootElement.getAttribute("step").equals("")) {
                config.setStep(Integer.parseInt(rootElement.getAttribute("step")));
            }


        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public Config getConfig() {
        return this.config;
    }

    public String getClasspathconfig() {
        return classpathconfig;
    }

    public void setClasspathconfig(String classpathconfig) {
        this.classpathconfig = classpathconfig;
    }

    public String getAbsolutepathconfig() {
        return absolutepathconfig;
    }

    public void setAbsolutepathconfig(String absolutepathconfig) {
        this.absolutepathconfig = absolutepathconfig;
    }

}

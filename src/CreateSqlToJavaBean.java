import java.util.ArrayList;
import java.util.List;

/**
 * @Author Sun
 * @Date 2020/6/25
 * @Describe 这是一个将sql转换为javaBean的简陋工具类
 * 转换的同时即可进行javaBean的输出，但……我不想改了^_^
 * 还有进行大量处理的单线程操作应该用StringBuffer的，这里也不改了……毕竟一来操作也不多，二来简陋使用也占用不了太大内存
 * 以及有兴趣的可以加上前端页面进行更复杂的操作
 */
public class CreateSqlToJavaBean {


    public static void main(String[] args) {
        String createSql = "CREATE TABLE `comment` (\n" +
                "  `comment_id` varchar(255) NOT NULL COMMENT '评论id',\n" +
                "  `user_id` bigint(255) DEFAULT NULL COMMENT '用户id',\n" +
                "  `replay_id` bigint(255) DEFAULT NULL COMMENT '被评论用户id',\n" +
                "  `post_id` varchar(255) DEFAULT NULL COMMENT '帖子id',\n" +
                "  `parent_comment_id` varchar(255) DEFAULT NULL COMMENT '父级评论id',\n" +
                "  `create_comment_time` datetime DEFAULT NULL COMMENT '评论创建时间',\n" +
                "  `like_comment_num` int(255) DEFAULT '0' COMMENT '评论点赞人数',\n" +
                "  `unlike_comment_num` int(255) DEFAULT '0' COMMENT 'bigint',\n" +
                "  PRIMARY KEY (`comment_id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String aPrivate = "";
        try {
            aPrivate = toJavaBean(convert(createSql, null, "private"));
        } catch (Exception e) {
            System.err.println("请注意：注释中不能有换行符！其他问题请排查！");
            e.printStackTrace();
        }
        System.err.println(aPrivate);


    }


    /**
     * @Describe 这是一个将数据库类型转换为java类型的枚举
     * 由于布尔值存储需要转换，因此这里将tinyint转为Integer类型(用byte包装类无法直接赋值....好像是)
     * 用基本类型时，如果数据库中没有设置默认值，查出null值时会出现异常，因此选用包装类型（数值）
     * 这里仅记录常用的数据类型，如有需要，自行添加
     */
    public enum JdbcTypeToJavaType {
        BIGINT("bigint", "Long"),
        CHAR("char", "char"),
        DATE("date", "Date"),
        DATETIME("datetime", "Date"),
        DECIMAL("decimal", "BigDecimal"),
        DOUBLE("double", "Double"),
        FLOAT("float", "Float"),
        INT("int", "Integer"),
        INTEGER("integer", "Integer"),
        JSON("json", "JSON"),
        TEXT("text", "String"),
        TIME("time", "Date"),
        TIMESTAMP("timestamp", "Date"),
        TINYINT("tinyint", "Integer"),
        VARCHAR("varchar", "String");


        /**
         * 数据库类型
         */
        private String jdbcType;
        /**
         * javaBean类型
         */
        private String javaType;

        JdbcTypeToJavaType(String jdbcType, String javaType) {
            this.jdbcType = jdbcType;
            this.javaType = javaType;
        }


        private static JdbcTypeToJavaType[] values = JdbcTypeToJavaType.values();

        /**
         * 根据数据库的数据类型获得对应的java类型，如果没有对应的会返回一个null值
         *
         * @param jdbcType 数据库内的数据类型
         * @return
         */
        public static String getJavaTypeByJdbcType(String jdbcType) {
            for (JdbcTypeToJavaType value : values) {
                if (jdbcType.equals(value.jdbcType)) {
                    return value.javaType;
                }
            }
            return null;
        }
    }

    /**
     * 输出为javaBean
     *
     * @param javaBeanVO
     * @return
     */
    public static String toJavaBean(JavaBeanVO javaBeanVO) {
        StringBuffer javaBean = new StringBuffer();
        javaBean.append("public class " + javaBeanVO.getClassName() + "{\n");
        //修饰符
        String modifier = javaBeanVO.getModifier();

        //注解集合
        List<String> annotations = javaBeanVO.getAnnotations();

        StringBuffer theAnnotation = new StringBuffer();
        if (annotations != null) {
            for (String annotation : annotations) {
                theAnnotation.append(annotation + "\n");
            }
        }

        //注释、类型和字段集合
        List<String> commentAndTypeAndParams = javaBeanVO.getCommentAndTypeAndParams();

        for (String commentAndTypeAndParam : commentAndTypeAndParams) {
            javaBean.append("\n");
            String[] split = commentAndTypeAndParam.split("/");
            //加上基本注释
            javaBean.append("\t/**\n" +
                    "\t* " + split[0] + "\n" +
                    "\t*/\n");

            //加上注解
            javaBean.append(theAnnotation);
            //加上修饰符 类型 字段
            javaBean.append("\t" + modifier + " " + split[1] + " " + split[2] + ";\n");

        }
        javaBean.append("}");
        return javaBean.toString();
    }

    /**
     * 转换
     *
     * @param createdSql  创建表的sql
     * @param annotations 字段上的注解集合
     * @param modifier    字段修饰符
     * @return
     */
    public static JavaBeanVO convert(String createdSql, List<String> annotations, String modifier) {
        String start = "CREATETABLE";
        JavaBeanVO javaBeanVO = new JavaBeanVO();
        createdSql = createdSql.replaceAll(" ", "");


        if (!createdSql.toLowerCase().startsWith(start.toLowerCase())) {
            throw new RuntimeException("请按规定的格式传入！\n" +
                    "示例：\n" +
                    "CREATE TABLE `user_props` (\n" +
                    "  `user_id` bigint(255) DEFAULT NULL COMMENT '用户id',\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        }
        createdSql = createdSql.replaceAll(start, "");
        String className = createdSql.substring(0, createdSql.indexOf("("));
        //类名
        className = toHumpRules(cleanSymbol(className, "`"), "big");

        createdSql = createdSql.substring(createdSql.indexOf("(") + 1, createdSql.lastIndexOf(")"));
        createdSql = createdSql.replaceAll("\r", "");

        if (createdSql.startsWith("\n")) {
            createdSql = createdSql.replaceFirst("\n", "");
        }

        String[] split = createdSql.split("\n");

        //java类型
        String javaType = "";
        List<String> commentTypeAndParams = new ArrayList<>();

        for (String s : split) {
            //去除尾部冗余
            if (s.contains("PRIMARYKEY") || s.contains("UNIQUEKEY") || s.contains("SPATIALKEY") || s.contains("FULLTEXTKEY") || s.contains("ENGINE")) {
                continue;
            }
            //获取javaType
            JdbcTypeToJavaType[] values = JdbcTypeToJavaType.values();
            String sentence = "";
            for (JdbcTypeToJavaType value : values) {
                //取得字段与注释之间的内容，防止其他内容影响类型的生成
                if (s.contains("NULL")) {
                    sentence = s.substring(s.lastIndexOf("`"), s.indexOf("NULL"));
                } else if (s.contains("DEFAULT")) {
                    sentence = s.substring(s.lastIndexOf("`"), s.indexOf("DEFAULT"));
                }

                if (sentence.contains(value.jdbcType)) {
                    javaType = value.javaType;
                    //由于bigint和int中都有int需要再次进行判断一下
                    if (sentence.contains(JdbcTypeToJavaType.BIGINT.jdbcType)) {
                        javaType = JdbcTypeToJavaType.BIGINT.javaType;
                    }
                }
            }

            //获取字段名
            String param = toHumpRules(cleanSymbol(s.substring(0, s.lastIndexOf("`")), "`"), "small");

            //获取注释
            String comment = "";
            if (s.contains("DEFAULT'")) {
                s = s.replaceFirst("DEFAULT'", "");
                s = s.replaceFirst("'", "");
            }
            if (s.contains("'")) {
                comment = s.substring(s.indexOf("'") + 1, s.lastIndexOf("'"));
            }

            //修饰符 类型 字段的拼接
            String commentTypeAndParam = comment + "/" + javaType + "/" + param;
            commentTypeAndParams.add(commentTypeAndParam);
        }

        //注解
        javaBeanVO.setAnnotations(annotations);
        //类名
        javaBeanVO.setClassName(className);
        //修饰符
        javaBeanVO.setModifier(modifier);
        //注释和类型及字段
        javaBeanVO.setCommentAndTypeAndParams(commentTypeAndParams);
        return javaBeanVO;
    }


    /**
     *
     */
    static class JavaBeanVO {
        /**
         * 类名
         */
        private String className;

        /**
         * 修饰符
         */
        private String modifier;


        /**
         * 注释、类型及字段集合
         */
        private List<String> commentAndTypeAndParams;

        /**
         * 注解集合
         */
        private List<String> annotations;


        public JavaBeanVO() {
        }

        public JavaBeanVO(String className, String modifier, List<String> commentAndTypeAndParams, List<String> annotations) {
            this.className = className;
            this.modifier = modifier;
            this.commentAndTypeAndParams = commentAndTypeAndParams;
            this.annotations = annotations;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getModifier() {
            return modifier;
        }

        public void setModifier(String modifier) {
            this.modifier = modifier;
        }

        public List<String> getCommentAndTypeAndParams() {
            return commentAndTypeAndParams;
        }

        public void setCommentAndTypeAndParams(List<String> commentAndTypeAndParams) {
            this.commentAndTypeAndParams = commentAndTypeAndParams;
        }

        public List<String> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(List<String> annotations) {
            this.annotations = annotations;
        }
    }

    /**
     * 这是一个去除字符串中指定字符的工具类
     *
     * @param word   单词
     * @param symbol 字符
     * @return
     */
    public static String cleanSymbol(String word, String symbol) {
        word = word.replace(symbol, "");
        return word;
    }

    /**
     * 获取某个字符在整个字符串内出现的次数
     *
     * @param word      单词
     * @param character 字符
     * @return
     */
    public static int getNumberOfCharacters(String word, char character) {
        char[] chars = word.toCharArray();
        int number = 0;
        for (char aChar : chars) {
            if (aChar == character) {
                number++;
            }
        }
        return number;
    }

    /**
     * 下划线转大小驼峰规则
     *
     * @param word   要转换的单词
     * @param status small--->小驼峰  big--->大驼峰
     * @return
     */
//    public static String toHumpRules(String word, String status) {
//        //获取下划线在单词中出现的次数
//        int numberOfCharacters = getNumberOfCharacters(word, '_');
//        //如果下划线出现的次数为0，将首字母大写并返回
//        if (numberOfCharacters == 0) {
//            if (status.equals("big")) {
//                word = word.replaceFirst(word.substring(0, 1), word.substring(0, 1).toUpperCase());
//            }
//            return word;
//        }
//        StringBuffer humpRulesWord = new StringBuffer(word);
//        if (status.equals("big")) {
//            //将首字母大写
//            humpRulesWord.replace(0, 1, humpRulesWord.substring(0, 1).toUpperCase());
//        }
//        //根据下划线出现的次数进行遍历
//        for (int i = 0; i < numberOfCharacters; i++) {
//            //将下划线后一位转为大写
//            humpRulesWord.replace(humpRulesWord.indexOf("_") + 1, humpRulesWord.indexOf("_") + 2, humpRulesWord.substring(humpRulesWord.indexOf("_") + 1, humpRulesWord.indexOf("_") + 2).toUpperCase());
//            //去除转化过大写前的下划线
//            humpRulesWord.replace(humpRulesWord.indexOf("_"), humpRulesWord.indexOf("_") + 1, "");
//        }
//        return humpRulesWord.toString();
//    }

    /**
     * 转驼峰
     *
     * @param word   要转换的单词
     * @param status big-大驼峰
     * @return
     */
    public static String toHumpRules(String word, String status) {
        word = word.toLowerCase();
        if (status.equals("big")) {
            word = word.replaceFirst(word.substring(0, 1), word.substring(0, 1).toUpperCase());
        }
        char[] chars = word.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '_') {
                String letter = String.valueOf(chars[i + 1]).toUpperCase();
                stringBuffer.append(letter);
                i++;
            } else {
                stringBuffer.append(chars[i]);
            }
        }
        return stringBuffer.toString();
    }


}

import java.util.*;

/**
 * @author aleo.liu
 * @version 1.0
 * @date 2021/1/23 上午9:15
 * @description
 */
public class SqlCreateUtil {

    /**
     * sql语句的String类型
     */
    private StringBuffer sql = new StringBuffer();

    /**
     * 要操作的字段,用在update insert中的Set之后,字段名称和值进行对应
     */
    private Map<String, Object> operateFields = new HashMap<String, Object>();

    /**
     * 要操作的值 ,同于insert语句中的values,不需要跟字段名称,只需要值就OK了
     */
    private ArrayList<Object> operateValues = new ArrayList<Object>();

    /**
     * select语句中用于select 后面的字段名称
     */
    private ArrayList<String> fields = new ArrayList<String>();

    /**
     * 条件集合,用于where语句后面 形式例如 field1=value1
     */
    private ArrayList<SqlCondition> conditions = new ArrayList<>();

    /**
     * 操作符,使用select update delete insert者四种操作符
     */
    private String operate = "";

    /**
     * 操作表 ,要操作的表
     */
    private String table = "";

    /**
     * 限制 要限制的长度
     */
    private String limit = "";

    /**
     * 排序规则,定制排序规则，SortMethod
     */
    private String order = "";

    private String group = "";

    /**
     * 默认构造方法使用select语句操作
     */
    public SqlCreateUtil() {
        this.operate = SqlOperate.SELECT;
    }

    /**
     * 初始化构造数据库操作,默认使用select查询
     */
    public SqlCreateUtil(String table) {
        this.table = table;
        this.operate = SqlOperate.SELECT;
    }

    public SqlCreateUtil(String operate, String table) {
        this.operate = operate;
        this.table = table;
    }

    public SqlCreateUtil operate(String operate){
        this.operate=operate;
        return this;
    }

    /**
     * 主要应用update和insert的数据库操作添加sql
     */
    public SqlCreateUtil operateFiled(String properName,Object properValue){
        operateFields.put(properName, properValue);
        return this;
    }

    public SqlCreateUtil operateFiled(Map<String, Object> fields){
        operateFields.putAll(fields);
        return this;
    }

    public SqlCreateUtil operateFiled(Object properValue){
        operateValues.add(properValue);
        return this;
    }

    /**
     * 主要应用于select语句中，用于添加抬头字段
     * @param @filed
     * @return
     */
    public SqlCreateUtil field(String field){
        this.fields.add(field);
        return this;
    }

    /**
     * 主要应用于select语句中，用于添加抬头字段，一次增加多个fields
     * @param @fields
     * @return
     */
    public SqlCreateUtil fields(String fields){
        String[] fieldArray = fields.split(",");
        Collections.addAll(this.fields, fieldArray);
        return this;
    }

    /**
     * 添加字段并可以给字段加别名
     * @param field
     * @param alias
     * @return
     */
    public SqlCreateUtil field(String field,String alias){
        this.fields.add(field+" as "+alias);
        return this;
    }
    /**
     * 添加字段
     * @param table
     * @return
     */
    public SqlCreateUtil table(String table){
        this.table=table;
        return this;
    }

    /**
     * 如果value不存在，则不添加
     * @param field
     * @param operator
     * @param value
     * @return
     */
    public SqlCreateUtil andCondition(String field,String operator,Object value){
        if (!ObjectUtils.isEmpty(value)){
            this.conditions.add(new SqlCondition(LogicalOperators.AND,operator,field,filterValue(value)));
        }
        return this;
    }

    public SqlCreateUtil orCondition(String field,String operator,Object value){
        if (!ObjectUtils.isEmpty(value)){
            this.conditions.add(new SqlCondition(LogicalOperators.OR,operator,field,filterValue(value)));
        }
        return this;
    }

    public SqlCreateUtil andLike(String field,String value){
        if (!ObjectUtils.isEmpty(value)){
            value = "%"+value+"%";
            andCondition(field,"like",value);
        }
        return this;
    }

    public SqlCreateUtil orLike(String field,String value){
        if (!ObjectUtils.isEmpty(value)){
            value = "%"+value+"%";
            orCondition(field,"like",value);
        }
        return this;
    }

    /**
     * 如果value不存在，则不添加
     * 一种条件场景 where id in (1,2,3) or where id not in (1,2,3)等
     * @param field
     * @param values
     * @return
     */
    public SqlCreateUtil andConditionIn(String field, List values){
        return conditionIn(LogicalOperators.AND,RelationalOperators.IN,field,values);
    }

    public SqlCreateUtil orConditionIn(String field, List values){
        return conditionIn(LogicalOperators.OR,RelationalOperators.IN,field,values);
    }

    public SqlCreateUtil andConditionNotIn(String field, List values){
        return conditionIn(LogicalOperators.AND,RelationalOperators.NOT_IN,field,values);
    }

    public SqlCreateUtil orConditionNotIn(String field, List values){
        return conditionIn(LogicalOperators.OR,RelationalOperators.NOT_IN,field,values);
    }

    private SqlCreateUtil conditionIn(String logicalOperators,String relationalOperators,String field,List values){
        if(!values.isEmpty()){
            StringBuilder conditionStr= new StringBuilder();
            conditionStr.append(" (");

            for (Object value:values){
                conditionStr.append(filterValue(value)).append(",");
            }

            conditionStr.delete(conditionStr.length()-1,conditionStr.length());

            conditionStr.append(")");

            this.conditions.add(new SqlCondition(logicalOperators,relationalOperators,field,conditionStr.toString()));
        }
        return this;
    }

    /**
     * 过滤值方法，如果是String类型和Date类型添加单引号，不是直接返回原值
     * @param value
     * @return
     */
    private String filterValue(Object value){
        if(value instanceof String){
            return "'"+value+"'";
        }
        else if(value instanceof Date){
            return "'"+value+"'";
        }
        else{
            return value.toString();
        }
    }

    public SqlCreateUtil orderBy(String sortMethod,String field){
        order= field+" "+sortMethod;
        return this;
    }

    public SqlCreateUtil groupBy(String... fields){
        StringBuilder groupBy = new StringBuilder();
        for (int i = 0;i<fields.length;i++){
            groupBy.append(fields[i]);
            if (i != fields.length-1){
                groupBy.append(",");
            }
        }
        group=groupBy.toString();
        return this;
    }

    public SqlCreateUtil limit(int start,int length){
        limit=start+","+length;
        return this;
    }

    /**
     * 重写toString方法,返回sql结果
     */
    public String toString(){
        sql.setLength(0);
        switch (operate) {
            case "select":
                return selectToSql();
            case "insert":
                return insertToSql();
            case "update":
                return updateToSql();
            case "delete":
                return deleteToSql();
            default:
                return null;
        }
    }

    public boolean conditionIsEmpty(){
        return conditions.isEmpty();
    }

    public void clearField(){
        fields.clear();
    }

    public void clearCondition(){
        conditions.clear();
    }

    /**
     * 添加字段
     * 例子：filed1,field2
     */
    private void assembleFiled(){
        if(fields.size()>0){
            //批量加字段
            for (String filed : this.fields) {
                //最后一个字段不加逗号
                if(!filed.equals(fields.get(fields.size()-1))){
                    sql=sql.append(" "+filed+",");
                }
                else{
                    sql=sql.append(" "+filed);
                }
            }
        }else{
            sql=sql.append(" *");
        }
    }

    /**
     * 添加条件
     * 例子：where condition1<condition2 and condition3>condition4
     */
    private void assembleCondition(){
        if(!conditions.isEmpty()){
            boolean first = true;
            sql=sql.append(" where");
            for (SqlCondition condition : conditions) {
                //判断是不是第一个
                if(first){
                    sql.append(" ").append(condition.getField())
                            .append(" ").append(condition.getRelationOperator())
                            .append(" ").append(condition.getValue());
                    first = false;
                }else{
                    sql.append(" ").append(condition.getLogicalOperator())
                            .append(" ").append(condition.getField())
                            .append(" ").append(condition.getRelationOperator())
                            .append(" ").append(condition.getValue());
                }
            }
        }
    }

    /**
     * 添加排序
     * 例子 order by field DESC
     */
    private void assembleOrder(){
        if(!order.isEmpty()){
            sql.append(" order by " +order);
        }
    }

    private void assembleGroup(){
        if(!group.isEmpty()){
            sql.append(" group by " +group);
        }
    }

    /**
     * 添加限制
     * 例子 limit 0,10
     */
    private void assembleLimit(){
        if(!limit.isEmpty()){
            sql.append(" limit "+limit);
        }
    }

    /**
     * 添加SET语句到sql变量中
     * 例子 SET key=value,key2=value2
     */
    private void assembleKeyValue(){
        if(!operateFields.isEmpty() ){
            sql=sql.append(" SET");

            for(String key :operateFields.keySet()){
                Object value=operateFields.get(key);
                sql=sql.append(" "+key+" = "+filterValue(value)+",");
            }

            //删除最后一个逗号
            sql=sql.deleteCharAt(sql.length()-1);
        }
    }

    /**
     * select语句转sql方法
     * @return 返回转好的字符串
     */
    private String selectToSql(){
        sql=sql.append(SqlOperate.SELECT);
        //字段处理，没有字段默认使用*
        assembleFiled();
        //表处理，暂时单表
        sql=sql.append(" from "+table);
        //where条件处理,有处理，无不处理
        assembleCondition();
        //分组语句
        assembleGroup();
        //排序语句
        assembleOrder();
        //限制条目
        assembleLimit();
        return sql.toString();
    }

    /**
     * insert语句转sql
     * @return 返回转好的字符串
     */
    private String insertToSql(){

        sql=sql.append(SqlOperate.INSERT);

        sql=sql.append(" "+table);

        //如果operateFields和operateValues只能取一个
        //operateFields注入sql语句的键值映射，operateValues只注入值，不注入键
        if(!operateFields.isEmpty() && operateValues.isEmpty()){
            assembleKeyValue();
        } else if(!operateValues.isEmpty() && operateFields.isEmpty()){
            sql=sql.append(" values(");

            for (Object value : operateValues) {
                sql=sql.append(" "+filterValue(value)+",");
            }

            //删除最后一个逗号并添加括号括上
            sql=sql.deleteCharAt(sql.length()-1).append(")");
        }

        return sql.toString();
    }

    private String updateToSql(){
        sql=sql.append(SqlOperate.UPDATE);
        sql=sql.append(" "+table);
        //operateFields注入sql语句的键值映射
        assembleKeyValue();
        //写入where语句
        assembleCondition();
        //分组语句
        assembleGroup();
        //排序语句
        assembleOrder();
        //限制条目
        assembleLimit();
        return sql.toString();
    }

    private String deleteToSql(){
        sql=sql.append(SqlOperate.DELETE);
        sql=sql.append(" from "+table);
        //写入where语句
        assembleCondition();
        //分组语句
        assembleGroup();
        //排序语句
        assembleOrder();
        //限制条目
        assembleLimit();
        return sql.toString();
    }
}

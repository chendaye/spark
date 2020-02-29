package sparkoffline.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.serde2.columnar.BytesRefWritable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class HBaseApp {
    Connection connection = null;
    Table table = null;
    Admin admin = null;

    String tabName = "hbase_java_api";

    @Before
    public void setUp(){
        Configuration configuration = new Configuration();
        configuration.set("hbase.rootdir", "hdfs://master:8020");
        configuration.set("hbase.zookeeper.quorum", "master:2181");
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();

            Assert.assertNotNull(connection);
            Assert.assertNotNull(admin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown(){
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConnection(){

    }

    @Test
    public void createTable() throws Exception{
        TableName tableName = TableName.valueOf(tabName);
        if(admin.tableExists(tableName)){
            System.out.println("表已经存在");
        }else{
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            hTableDescriptor.addFamily(new HColumnDescriptor("info"));
            hTableDescriptor.addFamily(new HColumnDescriptor("address"));
            admin.createTable(hTableDescriptor);
            System.out.println("create success");
        }
    }


    @Test
    public void queryTableInfo() throws Exception{
        HTableDescriptor[] tables = admin.listTables();
        if(tables.length > 0){
            for (HTableDescriptor table: tables){
                System.out.println(table.getNameAsString());

                HColumnDescriptor[] columnFamilies = table.getColumnFamilies();
                for (HColumnDescriptor columnDescriptor :columnFamilies){
                    System.out.println("\t"+columnDescriptor.getNameAsString());
                }
            }
        }
    }


    @Test
    public void testPut () throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));

        Put put = new Put(Bytes.toBytes("long"));

        //通过put设置要添加的数据
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("12"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("birthday"), Bytes.toBytes("12-01"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("company"), Bytes.toBytes("lengo"));

        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("country"), Bytes.toBytes("CN"));
        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("province"), Bytes.toBytes("HB"));
        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("city"), Bytes.toBytes("WH"));

        // 插入数据
        table.put(put);
    }

    @Test
    public void testPutMany() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        ArrayList<Put> puts = new ArrayList<>();

        //通过put设置要添加的数据
        Put put1 = new Put(Bytes.toBytes("girls"));
        put1.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("18"));
        put1.addColumn(Bytes.toBytes("info"), Bytes.toBytes("birthday"), Bytes.toBytes("12-01"));
        put1.addColumn(Bytes.toBytes("info"), Bytes.toBytes("company"), Bytes.toBytes("girls"));

        put1.addColumn(Bytes.toBytes("address"), Bytes.toBytes("country"), Bytes.toBytes("CN"));
        put1.addColumn(Bytes.toBytes("address"), Bytes.toBytes("province"), Bytes.toBytes("SH"));
        put1.addColumn(Bytes.toBytes("address"), Bytes.toBytes("city"), Bytes.toBytes("FK"));

        Put put2 = new Put(Bytes.toBytes("woman"));
        put2.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("21"));
        put2.addColumn(Bytes.toBytes("info"), Bytes.toBytes("birthday"), Bytes.toBytes("12-01"));
        put2.addColumn(Bytes.toBytes("info"), Bytes.toBytes("company"), Bytes.toBytes("wuman"));

        put2.addColumn(Bytes.toBytes("address"), Bytes.toBytes("country"), Bytes.toBytes("CN"));
        put2.addColumn(Bytes.toBytes("address"), Bytes.toBytes("province"), Bytes.toBytes("GZ"));
        put2.addColumn(Bytes.toBytes("address"), Bytes.toBytes("city"), Bytes.toBytes("SZ"));

        puts.add(put1);
        puts.add(put2);
        table.put(puts);
    }

    @Test
    public void testUpdate() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));

        //通过put设置要添加的数据
        Put put = new Put(Bytes.toBytes("girls"));

        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("20"));
        table.put(put);
    }

    @Test
    public void testGet01() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Get get = new Get("girls".getBytes()); // 默认得到所有列

        // 获取指定的列
        get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));

        Result result = table.get(get);
        printGet(result);
    }

    @Test
    public void testScan01() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        // 默认是取所有的
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testScan02() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        // 取 long 行 和 long行之后的记录；类似于 >=
        Scan scan = new Scan(Bytes.toBytes("long"));
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testScan03() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        // 取 long 行
        Scan scan = new Scan(new Get(Bytes.toBytes("long")));
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testScan04() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        // 取 long 行 到 woman行 ；类似 [long, woman )
        Scan scan = new Scan(Bytes.toBytes("long"), Bytes.toBytes("woman"));
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testScan05() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Scan scan = new Scan();
        // 取指定列
        ResultScanner scanner = table.getScanner(Bytes.toBytes("info"), Bytes.toBytes("age"));

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testScan06() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("info"));
        // 取指定列
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testScan07() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
        // 取指定列
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testFilter01() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Scan scan = new Scan();

        //筛选
        String reg = "^girl*";
        RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(reg));
        scan.setFilter(rowFilter);

        // 取指定列
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testFilter02() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Scan scan = new Scan();

        // 前缀匹配
        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes("g"));
        scan.setFilter(prefixFilter);

        // 取指定列
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    @Test
    public void testFilter03() throws Exception{
        // 拿到表
        Table table = connection.getTable(TableName.valueOf(tabName));
        Scan scan = new Scan();

        // 前缀匹配
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);  // 条件通过一个就可以
        PrefixFilter filter1 = new PrefixFilter("len".getBytes());
        PrefixFilter filter2 = new PrefixFilter("gir".getBytes());
        filterList.addFilter(filter1);
        filterList.addFilter(filter2);
        scan.setFilter(filterList);

        // 取指定列
        ResultScanner scanner = table.getScanner(scan);

        for (Result result: scanner){
            printGet(result);
        }
    }

    private void printGet(Result result){
        for (Cell cell: result.rawCells()){
            System.out.println(
                    Bytes.toString(result.getRow())+"\t"
                    +Bytes.toString(CellUtil.cloneFamily(cell))+"\t"
                    +Bytes.toString(CellUtil.cloneQualifier(cell))+"\t"
                    +Bytes.toString(CellUtil.cloneValue(cell))+"\t"
                    +cell.getTimestamp()
            );
        }
    }
}

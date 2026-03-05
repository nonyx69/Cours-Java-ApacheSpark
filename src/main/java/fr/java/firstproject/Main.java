package fr.java.firstproject;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.tukaani.xz.check.SHA256;

import java.util.Properties;

public class Main {

    public static String test = "test";
    public static String name = "Noa";
    public static String metier = "Etudiant";
    public static int age = 20;

    public static void main(String[] args) {

        System.out.println("Hello World!");

        String message = Main.myMessage(Main.name, Main.metier, Main.age);

        System.out.println(message);



        for (int i = 0; i < 10; i++){
            System.out.println("Etat : " +i);
        }

        String[] MesMots = {"test", "test2", "test3"};

        for (String mot : MesMots);


        //set Variable Global Jave (Windows)
        System.setProperty("hadoop.home.dir", "C:\\Hadoop\\hadoop-3.3.6");
        System.load("C:\\Hadoop\\hadoop-3.3.6\\bin\\hadoop.dll");

        //Init de Spark
        SparkSession spark = SparkSession.builder()
                .appName("Test Spark")
                .config("spark.master", "local[*]")
                .getOrCreate();
/*
        //creation du Dataset, init par un fichier texte
        Dataset<String> df = spark.read().textFile("test.txt");

        //afficher le nombre de ligne
        long count = df.count();
        System.out.println("=====================================");
        System.out.println("Nombre de ligne de notre fichier : " + count);
        System.out.println("=====================================");
*/


        //Connexion a DB
        String url = "jdbc:mysql://localhost:3306/sparkipssi";
        String user = "root";
        String password = "";

        Properties connectionProp = new Properties();
        connectionProp.put("user", user);
        connectionProp.put("password", password);
        connectionProp.put("driver", "com.mysql.cj.jdbc.Driver");

        System.out.println("=====================================");
        System.out.println("Connexion DB MYSQL");
        System.out.println("=====================================");

        //tout afficher
        Dataset<Row> dfMySQL = spark.read().jdbc(url, "user", connectionProp);

        dfMySQL.printSchema();
        dfMySQL.show();




        System.out.println("=====================================");
        System.out.println("Etape map");
        System.out.println("=====================================");


        Dataset<Row> dfMap = dfMySQL
                .select("id", "name", "country")
                .filter(dfMySQL.col("country").isNotNull());

        dfMap.show(5);


        System.out.println("=====================================");
        System.out.println("Etape Reduce et Shuffle");
        System.out.println("=====================================");

        Dataset<Row> dfReduce = dfMap.orderBy("country", "name");

        dfReduce.show(5);

        //exporté la donnée
        System.out.println("=====================================");
        System.out.println("Exportation de la data");
        System.out.println("=====================================");

        String chemin1ExportJSON = "export_user_by_country_json";

        dfReduce.write()
                .mode(SaveMode.Overwrite)
                .partitionBy("country")
                .json(chemin1ExportJSON);

        System.out.println("Export realiser : " + chemin1ExportJSON);





        //afficher les lignes quo'n souhaite
        Dataset<Row> dfTransform = dfMySQL.select("id", "name", "country");
        dfTransform.show(3);


        //exporté la donnée
        System.out.println("=====================================");
        System.out.println("Exportation de la data");
        System.out.println("=====================================");

        String cheminExportJSON = "export_user_json";
        dfTransform.write().mode(SaveMode.Overwrite).json(cheminExportJSON);

        System.out.println("Export realiser : " + cheminExportJSON);


        //vide la variable de Spark
        spark.stop();
    }

    public static String myMessage(String name, String metier, int age){

        return "Mon nom est " + name + " et je suis un " + metier + " et j'ai " + age + " ans";

    }
}

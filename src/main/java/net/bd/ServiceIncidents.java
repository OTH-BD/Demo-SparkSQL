package net.bd;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class ServiceIncidents {
    public static void main(String[] args) {
        SparkSession session = SparkSession.builder()
                .master("local[*]")
                .appName("ServiceIncidents")
                .getOrCreate();

        Dataset<Row> df = session.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv("incidents.csv");

        // Vue temporaire locale (plus simple à utiliser)
        df.createOrReplaceTempView("incidents");

        // Nombre d'incidents par service
        Dataset<Row> incidentsByService = session.sql(
                "SELECT service, COUNT(*) AS nb_incidents " +
                        "FROM incidents " +
                        "GROUP BY service"
        );
        incidentsByService.show();

        // Top 2 années avec le plus d'incidents
        Dataset<Row> topYears = session.sql(
                "SELECT year(TO_DATE(date)) AS year, COUNT(*) AS nb_incidents " +
                        "FROM incidents " +
                        "GROUP BY year " +
                        "ORDER BY nb_incidents DESC " +
                        "LIMIT 2"
        );
        topYears.show();

        session.stop();
    }

}

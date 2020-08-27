package be.apti.HibernateFX;

import be.apti.HibernateFX.model.Laptop;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HibernateFX extends Application {
    private static SessionFactory factory;


    @Override
    public void init() throws Exception {
        try {
            factory = new Configuration().configure().addPackage("be.apti.HibernateFX").addAnnotatedClass(Laptop.class).buildSessionFactory();
        } catch (HibernateException exception) {
            throw new ExceptionInInitializerError(exception);
        }
        addData(new Laptop("HP", "Laura", LocalDate.of(2018, 04, 02)));    
        getAllLaptops().forEach(System.out::println);
        updateLaptopByType("ACER", "LAURA");
        getAllLaptops().forEach(System.out::println);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane gridPane = new GridPane();
        javafx.scene.control.Button button = new javafx.scene.control.Button("Zoek");
        button.setPrefWidth(200.0);
        button.setPadding(new Insets(10, 10,10 ,10));
        button.setStyle("-fx-background-color: #c30009; -fx-text-fill: white;");
        javafx.scene.control.TextField textField = new TextField();
        textField.setPromptText("Zoeken");
        gridPane.add(textField, 1, 1);
        ListView<Laptop> listView = new ListView<Laptop>();
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton radioButton = new RadioButton("Dit");
        RadioButton radioButton1 = new RadioButton("Hide gridpane");
        radioButton1.setToggleGroup(toggleGroup);
        radioButton.setToggleGroup(toggleGroup);
        GridPane gridPane1 = new GridPane();
        gridPane1.add(radioButton, 1, 1);
        gridPane1.add(radioButton1, 1, 2);
        gridPane.add(gridPane1, 1, 5);
        radioButton.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("U heeft op 'Dit' gedrukt");
            alert.show();
        });
        radioButton1.setOnAction(actionEvent -> gridPane1.setVisible(false));


        listView.setPrefWidth(500);
        gridPane.add(listView, 0, 0);


        button.setOnAction(actionEvent -> {
            gridPane1.setVisible(true);
            List<Laptop> result = getLaptopByVendor(textField.getText());
            ObservableList<Laptop> observableList = FXCollections.observableList(result);
            listView.setItems(observableList);
            ComboBox comboBox = new ComboBox();
            comboBox.setItems(observableList);
            comboBox.valueProperty().addListener((ChangeListener<Laptop>) (observableValue, oud, nieuw) -> {
                ObservableList<Laptop> searchResult = FXCollections.observableList(List.of(nieuw));
                listView.setItems(searchResult);
            });
            gridPane.add(comboBox, 10, 10);
        });
        gridPane.add(button, 1, 2);
        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane, 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("HibernateFX!");
        stage.show();
    }

    private static void addData(Laptop laptop) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(laptop);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static List<Laptop> getAllLaptops() {
        List<Laptop> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);
                criteriaQuery.from(Laptop.class);
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }

    private static List<Laptop> getLaptopByVendor(String vendor) {
        List<Laptop> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("vendor"), vendor));
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }

    private static void deleteLaptopByType(String type) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("type"), type));
                session.createQuery(criteriaQuery).getResultList().forEach(session::delete);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static void updateLaptopByType(String type, String newType) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("type"), type));
                session.createQuery(criteriaQuery).getResultList().forEach(laptop -> {
                    laptop.setType(newType);
                    session.update(laptop);
                });
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
    }
}

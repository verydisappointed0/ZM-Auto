module com.adminpanel.zmauto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Database and ORM dependencies
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    opens com.adminpanel.zmauto to javafx.fxml;
    opens com.adminpanel.zmauto.controller to javafx.fxml;
    opens com.adminpanel.zmauto.model to javafx.fxml, org.hibernate.orm.core;
    opens com.adminpanel.zmauto.service to javafx.fxml;
    opens com.adminpanel.zmauto.util to javafx.fxml;

    exports com.adminpanel.zmauto;
    exports com.adminpanel.zmauto.controller;
    exports com.adminpanel.zmauto.model;
    exports com.adminpanel.zmauto.service;
    exports com.adminpanel.zmauto.util;
}

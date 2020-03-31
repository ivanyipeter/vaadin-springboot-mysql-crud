package com.mycompany.ui;

import com.mycompany.model.Person;
import com.mycompany.repository.PersonRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Component
@UIScope
public class PersonEditor extends VerticalLayout implements KeyNotifier {

    private final PersonRepository personRepository;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    private Person person;

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button serialize = new Button("Serialize", VaadinIcon.CHECK_CIRCLE.create());
    Button deSerialize = new Button("Deserialize", VaadinIcon.CHECK_CIRCLE.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete, serialize, deSerialize);

    Binder<Person> binder = new Binder<>(Person.class);
    private ChangeHandler changeHandler;

    @Autowired
    public PersonEditor(PersonRepository personRepository) {
        this.personRepository = personRepository;

        add(firstName, lastName, actions);

        binder.bindInstanceFields(this);

        setSpacing(true);

        save.getElement().getThemeList().add("save");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editPerson(person));
        serialize.addClickListener(e -> serializePersonList());
        deSerialize.addClickListener(e -> deSerializePersonList());


    }

    public interface ChangeHandler {
        void onChange();
    }

    public void save() {
        personRepository.save(person);
        changeHandler.onChange();
    }

    public void delete() {
        personRepository.delete(person);
    }

    public void editPerson(Person p) {
        if (p == null) {
            setVisible(false);
            return;
        }

        boolean isPersisted = p.getId() != null;
        if (isPersisted) {
            person = personRepository.findById(p.getId()).get();
        } else {
            person = p;
        }
        cancel.setVisible(isPersisted);
        binder.setBean(person);
        setVisible(true);
        firstName.focus();
    }

    public void serializePersonList() {

        List<Person> personList = personRepository.findAll();

        try {
            FileOutputStream fos = new FileOutputStream("Personlist");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(personList);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deSerializePersonList() {

        List<Person> personList = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream("Personlist");
            ObjectInputStream ois = new ObjectInputStream(fis);
            personList = (ArrayList) ois.readObject();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Person p : personList
        ) {
            System.out.println(p);

        }
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }

}

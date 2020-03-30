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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@UIScope
public class PersonEditor extends VerticalLayout implements KeyNotifier {

    private final PersonRepository personRepository;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    private Person person;

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

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

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }

}

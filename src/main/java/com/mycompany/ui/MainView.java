package com.mycompany.ui;

import com.mycompany.model.Person;
import com.mycompany.repository.PersonRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private PersonEditor personEditor;

    private PersonRepository personRepository;

    final Grid<Person> grid;

    final TextField filter;

    private final Button addNewButton;

    public MainView(PersonEditor personEditor, PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.personEditor = personEditor;
        this.grid = new Grid<>(Person.class);
        this.filter = new TextField();
        this.addNewButton = new Button("New Employee", VaadinIcon.PLUS.create());

        HorizontalLayout actions = new HorizontalLayout(addNewButton, filter);

        add(actions, grid, personEditor);

        grid.setHeight("200px");
        grid.setColumns("id", "firstName", "lastName");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        filter.setPlaceholder("Filter");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> grid.setItems(filterBy(filter.getValue())));

        grid.asSingleSelect().addValueChangeListener(e -> {
            personEditor.editPerson(e.getValue());
        });

        addNewButton.addClickListener(e -> personEditor.editPerson(new Person("", "")));
        personEditor.setChangeHandler(() -> {
            personEditor.setVisible(false);
            filterBy(filter.getValue());
        });

//        filterBy(null);
    }


    public List<Person> filterBy(String filter) {
        List<Person> allPerson = personRepository.findAll();
        List<Person> filtered = new ArrayList<>();
        if (filter.isEmpty()) {
            return allPerson;
        } else {
            for (Person p : allPerson
            ) {
                if (textFilter(p.getFirstName(), filter) || textFilter(p.getLastName(), filter) || textFilter(p.getId().toString(), filter)) {
                    filtered.add(p);
                }
            }
            return filtered;
        }
    }

    public boolean textFilter(String firstName, String filter) {
        if (firstName == null || firstName.isEmpty() || firstName.toLowerCase().contains(filter.toLowerCase())) {
            return true;
        }
        return false;
    }
}

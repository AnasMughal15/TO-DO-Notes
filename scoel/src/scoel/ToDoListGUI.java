package scoel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ToDoListGUI extends JFrame implements ActionListener {
    private static List<ToDoItem> currentList = new ArrayList<>();
    private DefaultListModel<ToDoItem> listModel = new DefaultListModel<>();
    private JList<ToDoItem> list = new JList<>(listModel);
    private JTextField inputField = new JTextField(20);
    private JButton addButton = new JButton("Add");
    private JButton removeButton = new JButton("Remove");
    private JButton removeAllButton = new JButton("Remove All");
    private JButton updateButton = new JButton("Update");
    private JLabel selectedItemLabel = new JLabel();
    private JSpinner prioritySpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private JButton setPriorityButton = new JButton("Set Priority");

    public ToDoListGUI() {
        super("To-Do List");
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(removeAllButton);
        inputPanel.add(updateButton);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel selectedPanel = new JPanel();
        selectedPanel.add(new JLabel("Selected Item:"));
        selectedPanel.add(selectedItemLabel);
        add(selectedPanel, BorderLayout.NORTH);

        JPanel priorityPanel = new JPanel();
        priorityPanel.add(new JLabel("Priority:"));
        priorityPanel.add(prioritySpinner);
        priorityPanel.add(setPriorityButton);
        add(priorityPanel, BorderLayout.EAST);

        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        removeAllButton.addActionListener(this);
        updateButton.addActionListener(this);
        setPriorityButton.addActionListener(this);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateSelectedItemLabel();
            }
        });

        list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteItem");
        list.getActionMap().put("deleteItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void updateSelectedItemLabel() {
        ToDoItem selectedItem = list.getSelectedValue();
        if (selectedItem != null) {
            selectedItemLabel.setText(selectedItem.toString());
            prioritySpinner.setValue(selectedItem.getPriority());
        } else {
            selectedItemLabel.setText("");
            prioritySpinner.setValue(0);
        }
    }

    private void removeSelectedItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            currentList.remove(selectedIndex);
            listModel.remove(selectedIndex);
            updateSelectedItemLabel();
            adjustPriority(); // Adjust the priorities after removing an item
        }
    }

    private void adjustPriority() {
        List<ToDoItem> sortedList = new ArrayList<>(currentList);
        sortedList.sort(Comparator.comparingInt(ToDoItem::getPriority)); // Sort the list based on priority

        currentList.clear();
        listModel.clear();

        for (ToDoItem item : sortedList) {
            currentList.add(item);
            listModel.addElement(item);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String itemText = inputField.getText();
                           if (!itemText.isEmpty()) {
                ToDoItem newItem = new ToDoItem(itemText);
                currentList.add(newItem);
                listModel.addElement(newItem);
                inputField.setText("");
                adjustPriority(); // Adjust the priorities after adding a new item
            }
        } else if (e.getSource() == removeButton) {
            removeSelectedItem();
        } else if (e.getSource() == removeAllButton) {
            currentList.clear();
            listModel.clear();
            updateSelectedItemLabel();
        } else if (e.getSource() == updateButton) {
            int selectedIndex = list.getSelectedIndex();
            String itemText = inputField.getText();
            if (selectedIndex != -1 && !itemText.isEmpty()) {
                ToDoItem selectedItem = currentList.get(selectedIndex);
                selectedItem.setText(itemText);
                listModel.setElementAt(selectedItem, selectedIndex);
                inputField.setText("");
                updateSelectedItemLabel();
            }
        } else if (e.getSource() == setPriorityButton) {
            int selectedIndex = list.getSelectedIndex();
            int priority = (int) prioritySpinner.getValue();
            if (selectedIndex != -1) {
                ToDoItem selectedItem = currentList.get(selectedIndex);
                selectedItem.setPriority(priority);
                adjustPriority(); // Adjust the priorities after changing the priority of an item
                updateSelectedItemLabel();
            }
        }
    }

    private class ToDoItem {
        private String text;
        private int priority;

        public ToDoItem(String text) {
            this.text = text;
            this.priority = 0; // Default priority is set to 0
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            return text + " (Priority: " + priority + ")";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ToDoListGUI();
            }
        });
    }
}



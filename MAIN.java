import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Book implements Serializable {
    private int bookID;
    private String title;
    private String author;
    private String genre;
    private boolean availability;
    private String borrower;

    public Book(int bookID, String title, String author, String genre) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availability = true;
        this.borrower = null;
    }

    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isAvailable() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    @Override
    public String toString() {
        return "Book ID: " + bookID + ", Title: " + title + ", Author: " + author + ", Genre: " + genre + ", Availability: " + (availability ? "Available" : "Not Available") + ", Borrower: " + (availability ? "None" : borrower);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Book other = (Book) obj;
        return bookID == other.bookID;
    }
}

class User implements Serializable {
    private int userID;
    private String name;
    private String contactInfo;
    private ArrayList<Book> borrowedBooks;

    public User(int userID, String name, String contactInfo) {
        this.userID = userID;
        this.name = name;
        this.contactInfo = contactInfo;
        this.borrowedBooks = new ArrayList<>();
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public synchronized void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public synchronized void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    @Override
    public String toString() {
        return "User ID: " + userID + ", Name: " + name + ", Contact Info: " + contactInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        return userID == other.userID;
    }
}

class Library implements Serializable {
    private ArrayList<Book> books;
    private ArrayList<User> users;
    private Map<Book, User> borrowedBooksMap;

    public Library() {
        books = new ArrayList<>();
        users = new ArrayList<>();
        borrowedBooksMap = new HashMap<>();
    }

    public synchronized void addBook(Book book) {
        books.add(book);
    }

    public synchronized void addUser(User user) {
        users.add(user);
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public Book findBookByAuthor(String author) {
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                return book;
            }
        }
        return null;
    }

    public User findUserByID(int userID) {
        for (User user : users) {
            if (user.getUserID() == userID) {
                return user;
            }
        }
        return null;
    }

    public synchronized void borrowBook(User user, Book book) {
        if (book.isAvailable()) {
            book.setAvailability(false);
            book.setBorrower(user.getName());
            borrowedBooksMap.put(book, user);
            user.borrowBook(book);
            JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Book not available.");
        }
    }

    public synchronized void returnBook(User user, Book book) {
        if (borrowedBooksMap.containsKey(book) && borrowedBooksMap.get(book).equals(user)) {
            book.setAvailability(true);
            book.setBorrower(null);
            borrowedBooksMap.remove(book);
            user.returnBook(book);
            JOptionPane.showMessageDialog(null, "Book returned successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Book not borrowed by this user.");
        }
    }

    public void displayBookInventory() {
        StringBuilder inventory = new StringBuilder();
        for (Book book : books) {
            inventory.append(book).append("\n");
        }
        JOptionPane.showMessageDialog(null, "Book Inventory:\n" + inventory.toString());
    }
}

class LibraryManagementSystemGUI {
    private static final String BOOKS_FILE = "books.txt";
    private static final String USERS_FILE = "users.txt";
    private Library library;

    private JFrame frame;
    private JPanel mainPanel;
    private JButton addBookButton;
    private JButton addUserButton;
    private JButton borrowBookButton;
    private JButton returnBookButton;
    private JButton searchBookByTitleButton;
    private JButton searchBookByAuthorButton;
    private JButton searchUserByIDButton;
    private JButton displayBookInventoryButton;

    public LibraryManagementSystemGUI() {
        loadLibrary();

        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 3, 5, 5));

        addBookButton = new JButton("Add Book");
        addUserButton = new JButton("Add User");

        borrowBookButton = new JButton("Borrow Book");
        returnBookButton = new JButton("Return Book");
        searchBookByTitleButton = new JButton("Search Book by Title");
        searchBookByAuthorButton = new JButton("Search Book by Author");
        searchUserByIDButton = new JButton("Search User by ID");
        displayBookInventoryButton = new JButton("Display Book Inventory");

        addBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        borrowBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                borrowBook();
            }
        });
        returnBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
        searchBookByTitleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBookByTitle();
            }
        });
        searchBookByAuthorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBookByAuthor();
            }
        });
        searchUserByIDButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchUserByID();
            }
        });
        displayBookInventoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayBookInventory();
            }
        });

        mainPanel.add(addBookButton);
        mainPanel.add(addUserButton);

        mainPanel.add(borrowBookButton);
        mainPanel.add(returnBookButton);
        mainPanel.add(searchBookByTitleButton);
        mainPanel.add(searchBookByAuthorButton);
        mainPanel.add(searchUserByIDButton);
        mainPanel.add(displayBookInventoryButton);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void loadLibrary() {
        library = new Library();

        try (BufferedReader brBooks = new BufferedReader(new FileReader(BOOKS_FILE));
             BufferedReader brUsers = new BufferedReader(new FileReader(USERS_FILE))) {

            String line;
            while ((line = brBooks.readLine()) != null) {
                String[] bookData = line.split(",");
                int bookID = Integer.parseInt(bookData[0]);
                String title = bookData[1];
                String author = bookData[2];
                String genre = bookData[3];
                boolean availability = Boolean.parseBoolean(bookData[4]);
                String borrower = bookData[5];
                Book book = new Book(bookID, title, author, genre);
                book.setAvailability(availability);
                book.setBorrower(borrower);
                library.addBook(book);
            }

            while ((line = brUsers.readLine()) != null) {
                String[] userData = line.split(",");
                int userID = Integer.parseInt(userData[0]);
                String name = userData[1];
                String contactInfo = userData[2];
                User user = new User(userID, name, contactInfo);

                // Check if user has borrowed books
                if (userData.length > 3) {
                    for (int i = 3; i < userData.length; i++) {
                        int bookID = Integer.parseInt(userData[i]);
                        Book borrowedBook = library.getBooks().stream().filter(b -> b.getBookID() == bookID).findFirst().orElse(null);
                        if (borrowedBook != null) {
                            user.borrowBook(borrowedBook);
                        }
                    }
                }

                library.addUser(user);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading library data!");
        }
    }


    private void saveLibrary() {
        try (BufferedWriter bwBooks = new BufferedWriter(new FileWriter(BOOKS_FILE));
             BufferedWriter bwUsers = new BufferedWriter(new FileWriter(USERS_FILE))) {

            // Save books
            for (Book book : library.getBooks()) {
                bwBooks.write(book.getBookID() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getGenre() + "," + book.isAvailable() + "," + book.getBorrower());
                bwBooks.newLine();
            }

            // Save users
            for (User user : library.getUsers()) {
                // Save user info
                bwUsers.write(user.getUserID() + "," + user.getName() + "," + user.getContactInfo());

                // Save borrowed books info
                StringBuilder borrowedBooksInfo = new StringBuilder();
                for (Book borrowedBook : user.getBorrowedBooks()) {
                    borrowedBooksInfo.append(",").append(borrowedBook.getBookID());
                }
                bwUsers.write(borrowedBooksInfo.toString());

                bwUsers.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving library data!");
        }
    }


    private void addBook() {
        String bookIDStr = JOptionPane.showInputDialog(frame, "Enter Book ID:");
        try {
            int bookID = Integer.parseInt(bookIDStr);
            String title = JOptionPane.showInputDialog(frame, "Enter Title:");
            String author = JOptionPane.showInputDialog(frame, "Enter Author:");
            String genre = JOptionPane.showInputDialog(frame, "Enter Genre:");

            Book book = new Book(bookID, title, author, genre);
            library.addBook(book);
            JOptionPane.showMessageDialog(frame, "Book added successfully!");
            saveLibrary();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid Book ID. Please enter a valid integer.");
        }
    }

    private void addUser() {
        String userIDStr = JOptionPane.showInputDialog(frame, "Enter User ID:");
        try {
            int userID = Integer.parseInt(userIDStr);
            String name = JOptionPane.showInputDialog(frame, "Enter Name:");
            String contactInfo = JOptionPane.showInputDialog(frame, "Enter Contact Info:");

            User user = new User(userID, name, contactInfo);
            library.addUser(user);
            JOptionPane.showMessageDialog(frame, "User added successfully!");
            saveLibrary();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid User ID. Please enter a valid integer.");
        }
    }

    private void borrowBook() {
        String userIDStr = JOptionPane.showInputDialog(frame, "Enter User ID:");
        String bookIDStr = JOptionPane.showInputDialog(frame, "Enter Book ID to borrow:");
        try {
            int userID = Integer.parseInt(userIDStr);
            int bookID = Integer.parseInt(bookIDStr);

            User user = library.findUserByID(userID);
            Book book = library.getBooks().stream().filter(b -> b.getBookID() == bookID).findFirst().orElse(null);

            if (user != null && book != null) {
                library.borrowBook(user, book);
                saveLibrary();
            } else {
                JOptionPane.showMessageDialog(frame, "Book not available or user not found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid User ID or Book ID. Please enter valid integers.");
        }
    }

    private void returnBook() {
        String userIDStr = JOptionPane.showInputDialog(frame, "Enter User ID:");
        String bookIDStr = JOptionPane.showInputDialog(frame, "Enter Book ID to return:");

        try {
            int userID = Integer.parseInt(userIDStr);
            int bookID = Integer.parseInt(bookIDStr);

            User user = library.findUserByID(userID);
            Book book = library.getBooks().stream().filter(b -> b.getBookID() == bookID).findFirst().orElse(null);

            if (user != null && book != null && user.getBorrowedBooks().contains(book)) {
                library.returnBook(user, book);
                saveLibrary();
            } else {
                JOptionPane.showMessageDialog(frame, "Book not found or not borrowed by this user.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid User ID or Book ID. Please enter valid integers.");
        }
    }

    private void searchBookByTitle() {
        String title = JOptionPane.showInputDialog(frame, "Enter Title to search:");
        Book book = library.findBookByTitle(title);
        if (book != null) {
            JOptionPane.showMessageDialog(frame, "Book found: " + book);
        } else {
            JOptionPane.showMessageDialog(frame, "Book not found.");
        }
    }

    private void searchBookByAuthor() {
        String author = JOptionPane.showInputDialog(frame, "Enter Author to search:");
        Book book = library.findBookByAuthor(author);
        if (book != null) {
            JOptionPane.showMessageDialog(frame, "Book found: " + book);
        } else {
            JOptionPane.showMessageDialog(frame, "Book not found.");
        }
    }

    private void searchUserByID() {
        String userIDStr = JOptionPane.showInputDialog(frame, "Enter User ID to search:");
        try {
            int userID = Integer.parseInt(userIDStr);
            User user = library.findUserByID(userID);
            if (user != null) {
                // Display user info along with borrowed books
                StringBuilder userInfo = new StringBuilder(user.toString());
                userInfo.append("\nBorrowed Books:");
                for (Book book : user.getBorrowedBooks()) {
                    userInfo.append("\n\t").append(book.getTitle()).append(" (ID: ").append(book.getBookID()).append(")");
                }
                JOptionPane.showMessageDialog(frame, userInfo.toString());
            } else {
                JOptionPane.showMessageDialog(frame, "User not found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid User ID. Please enter a valid integer.");
        }
    }

    private void displayBookInventory() {
        library.displayBookInventory();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LibraryManagementSystemGUI();
            }
        });
    }
}

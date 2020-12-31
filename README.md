# Javabase
Javabase is a utility dependency to allow easy database connections in Java.

**Table of Contents**
- [Installation](#installation)
  - [Dependency](#dependency)
  - [Connectors](#connectors)
- [Usage](#usage)
  - [Connecting](#connecting)
  - [Schemas](#schemas)
  - [Creating & Dropping Tables](#creating--dropping-tables)
  - [Objects](#objects)
  - [Inserting Data](#inserting-data)
  - [Selecting Data](#selecting-data)
  - [Updating Data](#updating-data)
  - [Deleting Data](#deleting-data)
  - [Converting to and from DatabaseValues](#converting-to-and-from-databasevalues)
  - [Raw SQL Statements](#raw-sql-statements)


## Installation
### Dependency
Installing Javabase is fairly simple. Just include it in your pom.xml like so:
```xml
<dependency>
    <groupId>com.visualfiredev</groupId>
    <artifactId>javabase</artifactId>
    <version>0.0.4</version>
</dependency>
```

To actually use Javabase, you must also include the SQL connector you wish to use
for your project. Here are three common ones:

### Connectors
It's strongly recommended to use [Maven Repository](https://mvnrepository.com/) to find the
latest version of each of these dependencies.

#### MySQL
```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.22</version>
</dependency>
```

#### MariaDB
```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>2.7.0</version>
</dependency>
```

#### SQLite
```
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.32.3.2</version>
</dependency>
```

## Usage
I believe in learning through examples, so while I explain a lot of what is happening,
most of the code will be provided through examples.

### Connecting
Connecting to a database is incredibly simple. Just put in the host, database name, and database type.
Then, call `Database#connect` with the username and password, and voilà. 
```java
Database database = new Database("localhost", "ice_cream", DatabaseType.MariaDB);
database.connect("root", "");
```

### Schemas
Javabase works entirely around the concept of "Schemas" to help define and use tables and columns.
It is highly recommended to define the primary schemas of your database in constant variables to be used
throughout your application.

#### Example 1: MySQL / MariaDB
In this example, I create a TableSchema for different ice cream flavors. I will create a table
called "ice_cream_flavors", with three columns: "Id", which is the primary key and will auto increment,
"Name", which is a VARCHAR with a length of 20, and "Sprinkles", which is a boolean value.
```java
TableSchema ice_cream_schema = new TableSchema("ice_cream_flavors",
    new ColumnSchema("Id", DataType.INTEGER).setPrimaryKey(true).setAutoIncrement(true),
    new ColumnSchema("Name", DataType.VARCHAR, 20),
    new ColumnSchema("Sprinkles", DataType.TINYINT, 1)
);
```
If your database is of the SQLite type, the incompatible data types will be automatically converted
to their nearest equivilent. So `DataType.VARCHAR` will become `DataType.TEXT` and `DataType.TINYINT`
will become `DataType.INTEGER`. This allows you to create one table schema for the entire database
and not have to worry about conversions if you choose to switch later.

#### Example 2: Selection Schema
Now let's say I wanted to select all of the Ice Cream Flavors, but I only wanted the name in the result.
This is actually incredibly simple. If we kept our schema from Example 1, we would only need to call
the following methods.

Note: `TableSchema#removeColumn` is case-sensitive.
```java
TableSchema select = mysql.clone().removeColumn("Id").removeColumn("Sprinkles");
```

Alternatively, you can re-create the TableSchema using the minimum required variables, and it will still work.
```java
TableSchema select = new TableSchema("ice_cream_flavors",
    new ColumnSchema("Name", DataType.TEXT)
);
```

#### Example 3: Foreign Keys
To support foreign keys, you must first create the TableSchema that contains the foreign key. Then
you can add it as an option in one of your columns:
```java
TableSchema purchase_schema = new TableSchema("purchases",
    new ColumnSchema("Id", DataType.INTEGER).setPrimaryKey(true).setAutoIncrement(true),
    new ColumnSchema("Ice_Cream_Id", DataType.INTEGER).setForeignKey(ice_cream_schema, ice_cream_schema.getColumn("Id"))
);
```
Javabase will automatically handle the creation of the constraint.

Note: When dropping tables, ensure to drop the tables in the reverse order to which they were
created, otherwise you may run into Foreign Key errors as you would in a CLI. For example,
if I tried to drop the `ice_cream_schema` before I dropped the `purchase_schema`, because
the `purchase_schema` depends on the `ice_cream_schema`, it would fail.

### Creating & Dropping Tables
Creating & dropping tables is extremely simple. Just pass the TableSchema to `Database#createTable`
or `Database#dropTable` and off you go. If you wish to use the modifier "IF NOT EXISTS", you set
these upon TableSchema creation. For a modifier similar to "OR REPLACE", a boolean can be passed
to `Database#createTable` as to whether or not it should replace existing tables.

#### Example 1: Creation
The following example will replace any existing tables and create a new one.
```java
database.createTable(ice_cream_schema, true);
```

### Objects
As a utility class, throughout Javabase you can optionally extend the `DatabaseObject` class. Upon
extending, any fields in the class not marked with 'transient' will be treated as a database value.

#### Example: Ice Cream Flavors
In this example, I will create a class called IceCreamFlavor that extends a `DatabaseObject`. It will
contain three values: an int "id", a String of "name", and a boolean "sprinkles".

We will be including a blank constructor because Javabase needs to be able to
create a new object with default values, then reflectively insert those values
after creation. It cannot determine the variables from the constructor.

Note: When inserting objects to the database, the primary key is ignored if it also set to auto increment.
This means that when we create a new IceCreamFlavor, our ID field will be "-1", because we don't know
what it will be.
```java
public class IceCreamFlavor extends DatabaseObject {

    // Define our MySQL table schema from before
    public static final TableSchema TABLE_SCHEMA = new TableSchema("ice_cream_flavors",
        new ColumnSchema("Id", DataType.INTEGER).setPrimaryKey(true).setAutoIncrement(true),
        new ColumnSchema("Name", DataType.VARCHAR, 20),
        new ColumnSchema("Sprinkles", DataType.TINYINT, 1)
    ).setOrReplace(true);

    // Our database variables
    private int id = -1;
    private String name;
    private boolean sprinkles;

    // The constructor for us to insert objects
    // Since ID will auto-increment, we don't need to provide it.
    public IceCreamFlavor(String name, boolean sprinkles) {
        super(TABLE_SCHEMA); // Super the TABLE_SCHEMA for the DatabaseObject class
        this.name = name;
        this.sprinkles = sprinkles;
    }

    // A blank constructor for DatabaseObject
    public IceCreamFlavor() { super(TABLE_SCHEMA); }

    // Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isSprinkles() {
        return sprinkles;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setSprinkles(boolean sprinkles) {
        this.sprinkles = sprinkles;
    }

}
```

This object will be used in many of the following examples.

### Inserting Data
Inserting data is extremely simple using the DatabaseObject we have now created.

#### Example: More Ice Cream
##### Using Objects
First, create a new object using our previous class. Here I create four ice cream flavors,
only one of which has sprinkles.
```java
IceCreamFlavor vanilla = new IceCreamFlavor("Vanilla", false);
IceCreamFlavor chocolate = new IceCreamFlavor("Chocolate", false);
IceCreamFlavor playDough = new IceCreamFlavor("Play Dough", false);
IceCreamFlavor mintChocolate = new IceCreamFlavor("Mint Chocolate", true);
```

Now we will insert these objects into the database. Since our class extends `DatabaseObject`,
it is as simple as this:
```java
database.insert(vanilla);
database.insert(chocolate);
database.insert(playDough);
database.insert(mintChocolate);
```

Note: Now that our objects are inserted, we should destroy them / stop using them. They
will not be kept up to date, and to get modern results, you should re-select them. Similarly,
if we kept all of them, the `ID` fields would all be set to -1.

##### Database Results & Database Values
If you wish not to use the `DatabaseObject`, Javabase provides an object called the
`DatabaseResult`, which will be returned for any database operation which contains values.

A `DatabaseResult` contains an ArrayList of `DatabaseValue` and many utility functions for
converting from columns to rows, as well as getting values. You can read more about this on
the Javadoc. However, the basic concept is this: The `DatabaseValue` class maps a column in
the database to a value. For example, if we have an IceCreamFlavor in the database with a name of
"Chocolate", one of the DatabaseValue's that will describe this object is ("Name", "Chocolate").

`DatabaseResult` and `DatabaseValue` are used internally to map object values, so
everything the library uses will support them.

Confusing? It may be better off using `DatabaseObject`'s instead.

If you wish to continue, the following examples shows how to insert a new strawbery
ice cream flavor into the database. Notice how we omit the "ID" field, since it will
auto increment.
```java
database.insert(ice_cream_schema,
    new DatabaseValue("Name", "Strawberry"),
    new DatabaseValue("Sprinkles", 0)
);
```


### Selecting Data
Selecting data uses two primary methods: `TableSchema#select` and `TableSchema#selectAll`. I will
show a few examples using the previous schemas we have created:

#### Example 1: Selecting Everything
##### Using Objects
Selecting data is a little more complicated than inserting it, since every time an
item is selected, we need to create a new `IceCreamFlavor`. To select everything,
we must first pass the table schema, and then the class we want the data to be mapped to.
```java
ArrayList<IceCreamFlavor> flavors = database.selectAll(IceCreamFlavor.TABLE_SCHEMA, IceCreamFlavor.class);
```

Now we can list all of them:
```java
for (IceCreamFlavor flavor : flavors) {
    System.out.println("ID: " + flavor.getId() + ", Name: " + flavor.getName(), + "Sprinkles: " + flavor.isSprinkles());
}
```

##### Using `DatabaseResult`
As stated before, since `DatabaseResult` and `DatabaseValue` are used internally,
everything you can do with objects you can do with results. Here is how to fetch
all the flavors:
```java
DatabaseResult<Result> flavors = database.selectAll(IceCreamFlavor.TABLE_SCHEMA);
```

You'll notice that it's a little bit simpler than before. However, the results are
crude, and are listed in a strange order. Thankfully, some utility methods are
provided to help us order the data a litle bit better:
```java
for (int i = 1; i <= flavors.getRowCount(); i++) {
    DatabaseValue[] row = flavors.getValuesForRow(i);
    System.out.println("Id: " + row[0].getData() + ", Name: " + row[1].getData() + ", " + row[2].getData());
}
```

As you can see, we fetch data row-by-row, returning an array of DatabaseValue's per row.
Important to note is that `DatabaseResult#getValuesForRow` uses a 1-based index, NOT a 0-based index.

#### Example 2: Selecting Some Things
##### Using Objects
Selecting some objects is a little more complicated. Due to the nature of SQL-based databases,
the implementation has the user insert raw SQL for the "where" statement. However, if you have
ever used SQL before, this should not be too much of an issue. We can still cast to our objects
like before, however we now provide an extra string for our where:
```java
ArrayList<IceCreamFlavor> results = database.select(IceCreamFlavor.TABLE_SCHEMA, "NAME = 'Chocolate'", IceCreamFlavor.class);
if (results.size() == 0) {
    System.out.println("No results!");
    return;
}
IceCreamFlavor chocolateDb = results.get(0);
```

Here we selected the "chocolate" IceCreamFlavor. We can now use this class for UPDATE statements,
because it contains a valid primary key.

##### Using `DatabaseResult`
It's basically the same thing as before, but with the WHERE statement:
```java
DatabaseResult results = database.select(IceCreamFlavor.TABLE_SCHEMA, "NAME = 'Strawberry'");
if (results.getValues().length == 0) {
    System.out.println("No results!");
    return;
}
DatabaseValue[] strawberryDb = results.getValuesForRow(1);
```

Notice how we relate the array of DatabaseValue's to "strawberryDb",
similar to the "Using Objects" example in this section. Are you starting
to notice a connection between Values and Objects?

### Updating Data
Updating data is about as simple as selecting data.

#### Example: Updating Our Flavors
##### Using Objects
Updating data using objects can be very simple, but there's a catch: if your
object does not have a valid primary key, it will fail to update. This is why
we fetched our `chocolateDb` object from before, instead of just using the `chocolate`
object we had already created.

Here, we will be updating chocolate to have sprinkles:
```java
chocolateDb.setSprinkles(true);
database.update(chocolateDb);
```
Simple eh?

##### Using `DatabaseValue`
Notice how instead of DatabaseResults, we will now use DatabaseValues. If you
inserted data using this method, it's an extremely similar method for updating,
however now we have a "WHERE" clause to determine what we wish to update.

In this example, we set the "Strawberry" flavor to have sprinkles:
```java
database.update(IceCreamFlavor.TABLE_SCHEMA, "NAME = 'Strawberry'",
    new DatabaseValue("Sprinkles", 1)
);
```

### Deleting Data
Deleting data is as essential as inserting data. Luckily, this library makes it extremely simple as well.

#### Example: Removing A Flavor
For this example, imagine we have already selected a `vanillaDb` object, similar
to our `chocolateDb` object from before.

##### Using "Where"
Using a where statement to delete something from the database is recommended over using objects,
due to its ability to have more control over what is selected, and removing the requirement for
objects to be 100% identical.

An example of removing the vanilla flavor from the database works as follows:
```java
database.delete(IceCreamFlavor.TABLE_SCHEMA, "NAME = 'Vanilla'");
```

##### Using Objects
To remove an object, you must have an object that is 100% identical to the one you wish
to remove. Since this `vanillaDb` object is 100% identical to what's in the database
(because we just selected it), we can now call the method, passing our object:
```java
database.delete(vanillaDb);
```
Because this method requires the objects to be identical to what's in the database,
it is actually recommended to use a "WHERE" statement instead.

##### Using DatabaseValues
Surprise! You actually can't use DatabaseValues or DatabaseResults to remove data.

### Converting to and from DatabaseValues
Hello! If you're just joining us from DatabaseValues, please go back to the "Inserting Data"
section, and read the "Database Results & Database Values" section. This will describe
a fundamental understanding of how Javabase works.

With that out of the way, if you were following along with the previous examples, you may
have noticed a connection between DatabaseValue's and object fields. If so, congratulations!
The method Javabase uses to map Object Field's to the database is actually two utility
methods in the `DatabaseValue` class: `DatabaseValue#toObject` and `DatabaseValue#fromObject`.

Similarly, Javabase then wraps these classes for `DatabaseObject`, which makes it
easier to convert multiple fields/values at once. These methods are called
`DatabaseValue#fromValues` and `DatabaseObject#toValues`.

#### Examples
For all of the following examples, assume we have received a DatabaseResult by doing so:
```java
DatabaseResult result = database.select(IceCreamFlavor.TABLE_SCHEMA, "NAME = 'Strawberry'");
```

To convert from database values to an object, we do the following:
```java
DatabaseValue[] strawberryDbValues = result.getValuesForRow(1);
IceCreamFlavor strawberryDb = IceCreamFlavor.fromValues(IceCreamFlavor.TABLE_SCHEMA, strawberryDbValues, IceCreamFlavor.class);
```

To convert from an object to a database value array, it's quite a bit simpler:
```java
DatabaseValue[] strawberryDbValues2 = strawberryDb.toValues();
```

Another utility class is `DatabaseResult#toObjects`, which allows you to easily convert
an entire result to an array of objects:
```java
ArrayList<IceCreamFlavor> flavorResults = result.toObjects(IceCreamFlavor.TABLE_SCHEMA, IceCreamFlavor.class);
```
Though usage of this is discouraged, since it's basically just an alias for selecting data by
passing the object class.

### Raw SQL Statements
You can also run raw SQL on the database, if required at any point in time. Javabase allows
this through three primary methods: `Database#rawUpdate`, `Database#rawQuery`, and `Database#raw`,
all relating to the Java-implemented methods of `Connection#executeUpdate`, `Connection#executeQuery`,
and `Connection#execute` respectively.

Of note, when using `Database#rawQuery`, it returns a java-based `ResultSet`. You can
easily convert this to a `DatabaseResult` by passing it as a constructor:
```java
DatabaseResult result = new DatabaseResult(resultSet);
```

### That's all folks!
Congratulations for completing the usage guide! Now give yourself a pat on the back,
and create something amazing.

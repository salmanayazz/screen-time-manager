## Database implementation

Relational Databases are an abstraction over the file reading and writing. SQLite is a implementation of relational
databases in C language. SQLite is self contained, efficient and overall fast way to storing information with similar
performance compared to fopen and fwrite set of system calls.

Room is a Google owned another layer of abstraction over the SQLite intended for Android systems.

Additions to the project:
1. Dependencies for Room and SQLite from Android documentation. Optional dependencies have been commented out.
2. Database class with a singleton pattern to ensure only one instance of the database is created.

### Database Design
    ## Unified Database Model (For Both Frontend and Backend)

### App Entity
- **appId:** Primary Key, Integer, Auto-generated for frontend, manually set or auto-generated for backend.
- **appName:** String.
- **userId:** Foreign Key, Integer, Nullable, References User.
- **appTimeLimitHour:** Integer.
- **appTimeLimitMinute:** Integer.

### User Entity
- **userId:** Primary Key, Integer, Auto-generated for frontend, manually set or auto-generated for backend.
- **firstName:** String.
- **lastName:** String.
- **email:** String.
- **password:** String (Hash stored securely).
- **profilePicture:** String (this is a URI or path to the image).

### Usage Entity
- **usageId:** Primary Key, Integer, Auto-generated for frontend, manually set or auto-generated for backend.
- **appId:** Foreign Key, Integer, Non-Nullable, References App.
- **startTime:** Long (a timestamp format).
- **endTime:** Long (a timestamp format).

# DoHit 📝

**DoHit** is an advanced task management application, developed as part of an Android Studio Kotlin development project. The app leverages modern design principles and reactive programming to deliver a seamless experience for managing tasks effectively.

---

## Features 🚀

* **Task Management**: Add, edit, and delete tasks with detailed information (title, description, category, and due date).
* **Dynamic Task List**: Real-time updates to the task list using LiveData.
* **Task Details View**: View full details of a task in a separate screen.
* **Local Data Storage**: Data persistence using Room Database.
* **Modern Navigation**: Navigate between screens using Navigation Component.
* **Bilingual Interface**: Supports both Hebrew and English with automatic text direction (RTL and LTR).
* **Orientation Support**: Fully responsive design for both portrait and landscape modes.

---

## Screenshots 📸

| **Screen**                     | **Description**                                                                                  |
|---------------------------------|----------------------------------------------------------------------------------------------|
| **Main Screen**                 | Displays the list of tasks with options to add, edit, or delete tasks.                        |
| **Add/Edit Task Screen**        | A form to add or edit tasks with fields for title, description, category, due date, and image.|
| **Task Details Screen**         | Shows detailed information about a selected task, including its title, description, and more.|
| **Settings Screen**             | Allows users to customize app preferences, such as language and notifications.               |
| **Profile Screen**              | Displays user statistics, task completion rates, and other personal data.                    |
| **My Folders Screen**           | Shows categorized folders for organizing tasks by themes or priorities.                      |
| **Folder Details Screen**       | Lists all tasks associated with a selected folder, with options for interaction.             |
| **Delete Confirmation Dialog**  | Alerts users to confirm task deletion.                                                       |
| **Task Creation Confirmation Dialog** | Notifies users about successful task creation.                                        |


---

## Technologies 🛠️

* **Programming Language**: Kotlin.
* **Architecture**: MVVM (Model-View-ViewModel) with Repository pattern.
* **Data Persistence**: Room Database.
* **Reactive Programming**: LiveData and Coroutines.
* **Navigation**: Android Navigation Component.
* **UI Design**: Material Design principles.
* **Localization**: Full support for RTL and LTR layouts.

---

## Installation 📦

1. Clone the repository:
   ```bash
   git clone https://github.com/username/DoHit.git
2. Open the project in **Android Studio**.
3. Sync Gradle to download dependencies.
4. Run the app on an emulator or connected device.

### Requirements:

* **Minimum Android Version**: API Level 21 (Android 5.0 Lollipop).
* **Dependencies**:
  * AndroidX libraries for ViewModel, LiveData, and RecyclerView.
  * Room Database for local storage.
  * Navigation Component for screen transitions.

---

## Future Enhancements 🚀

* **Cloud Sync**: Integrate with cloud storage services for task synchronization.
* **User Profiles**: Enable user accounts with personalized task lists.
* **Advanced Search**: Add filters for categories, priorities, and completion status.
* **Improved UI**: Enhance user interface with animations and transitions.

---

## Lessons Learned 🎓

* **MVVM Architecture**: Improved understanding of clean code practices with proper layer separation.
* **Localization Challenges**: Handling bidirectional text and layout for a bilingual interface.
* **Room Integration**: Leveraged Room Database for efficient local storage.

---

## Acknowledgments 💡

This project was developed as part of the Android development course at HIT.  
We appreciate the guidance and support from our instructors and peers during this journey.

---

Feel free to contribute or provide feedback. Let's build **DoHit** together! 😊



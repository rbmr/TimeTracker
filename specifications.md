I was using this app on my phone to clock in and out for work, the only real goal is to keep track of the amount of hours I had worked, and when, and maybe make a note of what I worked on. Thats it. But I was apparently on a free trial, and now get loaded with ads. I have decided this is a fun project to learn how making an APK on android works. 

The project will be called "TimeTracker"

I shall use Kotlin Multiplatform with Compose Multiplatform. Focusing on android first, iOS second. I will be making and testing the app on a linux mint machine.

The goal is for the app to remain simple, and be easily installable. Id like to be able to send the app to friends for them to use it aswell, or have people be able to install it from my GitHub.

Here are the requirements for the app:

The entire system revolves around "WorkSessions". 
- A WorkSession has a start time, an optional end time, and an optional note.
- If a WorkSession is missing an end time, it is still "ongoing". Otherwise it is "historical".
- The logic of the app should ensure no more than one "ongoing" session exists.
- It is allowed for working sessions to overlap, this should only happen if users modify start and end themselves, but this not blocked.
- Ending a an ongoing session is equivalent to assigning and persisting an end time.
- When editing a WorkSession in any way, changes are persisted immediately, per attribute.
  - Example: I just wrote updated the note, changes are persisted to disk.

Dates and Times:
- In order to ensure timezone robusticity, we store times as milliseconds since epoch.
- When displaying a difference in time, we format it as HH:MM:SS, maxing out at 99:59:59 to prevent character overflow.
- When displaying a point in time, we use the devices local timezone to get the date and time, if no such timezone is available we use UTC.
  - We shall display date and time separately, date left, time right. 
  - This way, date and time may be edited independently as well.
  - We handle non-existent or ambiguous datetime that exist as a consequence of DST in a reasonable way:
    - user input of a non-existing datetime is shifted forward.
    - user input of ambiguous datetime, takes the earlier datetime.

We have 4 pages:
  1. Home
  2. Working
  3. History
  4. Edit

(1) Home:
- Home has two buttons: Punch in (1 -> 2), and History (1 -> 3).
- The Punch in button is big and clear.
- The punch in button is labeled "Punch in" if no ongoing task exists, and "Resume" otherwise.
- The History button is smaller and a bit dim (grey).

(2) Working:
- Opening the Working page, loads the ongoing session if it exists, or creates it.
- When creating a session, 
  - the start time defaults to the current time. 
  - the end time defaults to None.
  - Notes default to empty.
- We show from top to bottom:
  1. Left to right: Back button, "Working", Delete button
  2. the time you have been working. 
  3. the start date and time (editable)
  4. The note button
  5. the "Punch out" button.
- The punch out button brings you to the edit page for the ongoing session.
- The back button takes you to the home page.
  - Note that the aforementioned persistence rule means that opening the working page again after 

- The delete button deletes the ongoing session, and takes you to the home page.

(3) Editing:
- We show from top to bottom:
  1. Left to right: Back button, "Edit Session", Delete button
  2. the time worked, 
  3. the start date and time (editable), 
  4. the end date and time (editable), 
  5. The note button
  6. Save button.
- This page behaves slightly differently based on the underlying session (ongoing vs historical).
- For ongoing sessions:
  - we default the end time to the current time.
  - editing any field EXCEPT for the end datetime immediately persists the change.
  - clicking back takes you back to the working page.
  - delete removes the session and sends you back to the home page.
  - save persists the end datetime and sends you to the history page.
- For historical sessions:
  - editing ANY field immediately persists the change.
  - clicking back takes you back to the history page.
  - delete removes the session and sends you back to the history page.
  - save does the same as back, as all changes are already applied, taking you back to the history page.

(4) History:
- History shows the entire list of tasks as an list in full.
- Each row just shows the start datetime, and end datetime, and the first something characters of the note cropped off. 
- Clicking on the row brings you to the edit page for the task.
- Export opens the share window to share or save the entire history as a csv (start iso8601 datetime, and end iso8601 datetime, and notes in full). The item being shared should be a file, not the file contents as text.

General:
- The entire app is intended to be used in vertical mode. All the formatting (top to bottom, left to right ordering) remains the same in landscape mode.
- Datetime's should always be made and stored timezone aware using the devices timezone, or defaulting to utc.
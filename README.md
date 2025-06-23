
# Shangri-La Petition Platform (SLPP)

## Overview

The Shangri-La Petition Platform (SLPP) is a web/mobile application that empowers citizens of Shangri-La to propose and sign petitions on matters within the government's responsibility. Petitions that meet a signature threshold will qualify for parliamentary debate, followed by an official response from the Shangri-La Parliament.

## Features

### Petitioner Features
- Register using Email, Full Name, Date of Birth, Password, and Biometric ID (BioID) or scan QR code for BioID.
- Login to the platform to:
  - Create new petitions (with title and content).
  - View all petitions with details, status (open/closed), and any official response.
  - Sign open petitions created by others (can only sign once per petition).
  - View petition responses for closed petitions.
- Passwords are securely hashed and stored.
- BioID must be unique, valid (provided via dataset or QR code), and unused.

### Petitions Committee (Admin) Features
- Login using:
  - Email: `admin@petition.parliament.sr`
  - Password: `2025%shangrila`
- Set the global signature threshold.
- View all petitions (open/closed) with total signatures.
- Provide an official response to petitions meeting the threshold.
- Once responded, petitions are closed (cannot be signed further).

### Error Handling
- Displays clear error messages for:
  - Invalid BioID entry or scanning.
  - BioID already used.
  - Email already registered.
  - Invalid username or password during login.

## Usage Instructions

### Registration (as Petitioner)
1. Provide:
   - Unique Email
   - Name
   - Date of Birth (18+ required)
   - Password (1+ special character & digit)
   - BioID (via entry or QR code scanning)
2. Login after successful registration.

### Petitioner Dashboard
- View petitions, create new petitions, sign open petitions.

### Admin Dashboard
- Set/adjust signature threshold.
- Provide official responses for qualified petitions.

### QR Code Scanning (Optional)
- Requires physical device for QR scanning functionality.

## BioIDs
- A dataset of valid BioIDs provided via `BioIDs.txt` file.
- QR versions of these BioIDs are provided in the `BioID_QR_codes` folder.

## Login Information

**Admin Account**
- Email: `admin@petition.parliament.sr`
- Password: `2025%shangrila`

## Installation & Setup
1. Clone the repository or download source files.
2. Run the app on android studios.
3. Use QR code scanning feature for BioID (if applicable on physical devices).

## Technologies Used
- Frontend: Android (Java/Xml)
- Database/Backend: Firebase

## License
This project is developed as coursework for academic purposes.

---

# Demo

## Screen

Below is one demo screenshot for reference:

![Demo Screenshot](Media/startPage.jpg)

Other screenshots are available in the **media** folder.

The **demo video** will be uploaded soon.

---
**Developed for Mobile And Web Applications Module, University Of Leicester, 2025 by Vivek Shah**

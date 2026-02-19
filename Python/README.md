# Veriflow: BEC Payment Workflow Guard 

**Veriflow** is a robust cybersecurity solution designed to protect Small and Medium Enterprises (SMEs) from **Business Email Compromise (BEC)** and invoice fraud. By introducing a multi-layered verification process and an out-of-band (OOB) confirmation layer, Veriflow ensures that payments are only made to legitimate vendors.

## The Problem
BEC attacks are responsible for billions in losses annually. Attackers compromise business emails, intercept invoice threads, and swap legitimate bank account details (IBANs) with their own. Since the communication stays within the trusted email thread, employees often process these fraudulent payments unknowingly.

## Our Solution: The "Double-Check" Architecture
Veriflow acts as a security middleware between your email and your payment execution.
1.  **Automated OCR Analysis:** Every incoming invoice is scanned using OCR to extract the IBAN and vendor details.
2.  **Intelligence Engine:** The system compares extracted data against a database of verified partners.
3.  **Out-Of-Band (OOB) Verification:** If a mismatch or a new IBAN is detected, the system triggers a confirmation request via a secondary, non-email channel (Telegram/Mobile).
4.  **Audit-Ready Trails:** Every action is logged, providing a tamper-proof history of who approved what and when.

## Tech Stack
- **Frontend:** Angular (Responsive Dashboard)
- **Backend:** Spring Boot / Node.js (REST API & Business Logic)
- **OCR Engine:** Python (EasyOCR / PyMuPDF)
- **Database:** PostgreSQL (Audit logs & Partner data)
- **OOB Channel:** Telegram Bot API

## The Team
* **Milan Lazarevic** – Backend Developer / Security Logic
* **Ivana Radovanovic** – Frontend Developer / UX Designer
* **Milica Stanojlovic** – DevOps & Integration (OCR & Telegram Bot)
* **Marina Ivanovic** – Product Manager & Quality Assurance

## Key Features
- **Live Risk Dashboard:** Real-time monitoring of incoming financial documents.
- **IBAN Anomaly Detection:** Instant flagging of unauthorized bank account changes.
- **Tamper-Proof Audit Log:** Detailed record of the verification lifecycle for compliance.
- **Secondary Channel Confirmation:** Secure authorization that bypasses compromised email servers.

## How to Run
1.  **Clone the repo:** `git clone https://github.com/IvanaaXD/veriflow.git`
2.  **Start Backend:** `[Command for your backend, e.g., ./mvnw spring-boot:run]`
3.  **Start Frontend:** `npm install && npm start`
4.  **Configure Bot:** Add your Telegram Bot Token in `config.properties`.

---
*Developed for the 201 Hackathon 2026.*
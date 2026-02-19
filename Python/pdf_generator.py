import os
from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas
from reportlab.lib.colors import HexColor
from datetime import datetime

if not os.path.exists("test_invoices"):
    os.makedirs("test_invoices")

def generate_invoice(filename, vendor_name, vendor_pib, iban, amount, invoice_date, is_fraud=False):
    c = canvas.Canvas(f"test_invoices/{filename}", pagesize=A4)
    width, height = A4
    
    c.setFont("Helvetica-Bold", 24)
    c.drawString(50, height - 80, "INVOICE / FAKTURA")
    
    # Fraud marker (Samo za vas interno, da znate koji je koji fajl)
    if is_fraud:
        c.setFont("Helvetica", 10)
        c.setFillColor(HexColor("#FF0000"))
        c.drawString(450, height - 80, "[TEST: FRAUD]")
        c.setFillColor(HexColor("#000000"))

    c.setFont("Helvetica-Bold", 14)
    c.drawString(50, height - 130, vendor_name)
    c.setFont("Helvetica", 12)
    c.drawString(50, height - 150, f"PIB: {vendor_pib}")
    c.drawString(50, height - 170, "Adresa: Bulevar Oslobodjenja 12, Novi Sad")
    c.drawString(50, height - 190, "Email: finance@company.com")

    c.line(50, height - 210, width - 50, height - 210)

    c.setFont("Helvetica-Bold", 12)
    c.drawString(50, height - 240, "Invoice Number: INV-2024-001")
    c.drawString(50, height - 260, f"Date: {invoice_date}")
    c.drawString(50, height - 280, "Due Date: " + invoice_date)
    
    c.setFont("Helvetica-Bold", 12)
    c.drawString(50, height - 330, "Description")
    c.drawString(400, height - 330, "Amount (EUR)")
    c.line(50, height - 340, width - 50, height - 340)

    c.setFont("Helvetica", 12)
    c.drawString(50, height - 365, "Software Development Services")
    c.drawString(400, height - 365, amount)
    c.line(50, height - 380, width - 50, height - 380)

    c.setFont("Helvetica-Bold", 14)
    c.drawString(300, height - 420, "Total Due:")
    c.drawString(400, height - 420, f"EUR {amount}")

    c.setFont("Helvetica-Bold", 14)
    c.drawString(50, height - 500, "PAYMENT INSTRUCTIONS")
    c.setFont("Helvetica", 12)
    c.drawString(50, height - 520, f"Beneficiary: {vendor_name}")
    c.drawString(50, height - 540, "Bank: Raiffeisen Banka a.d. Beograd")
    
    c.setFont("Helvetica-Bold", 14)
    c.drawString(50, height - 565, f"IBAN: {iban}")

    c.setFont("Helvetica", 10)
    c.drawString(50, 50, "Thank you for your business!")

    c.save()
    print(f"Generisano: {filename}")


today_date = datetime.now().strftime("%Y-%m-%d")

# 1. PRAVA FAKTURA
generate_invoice(
    filename="1_Tehnomanija_Legitimno.pdf",
    vendor_name="Tehnomanija d.o.o.",
    vendor_pib="100000001",
    iban="RS35 2650 0000 0000 0000 12", # PRAVI IBAN
    amount="4,500.00",
    invoice_date=today_date,
    is_fraud=False
)

# 2. HAKOVANA FAKTURA (BEC Scenario)
generate_invoice(
    filename="2_Tehnomanija_HAKOVANO_BEC.pdf",
    vendor_name="Tehnomanija d.o.o.",
    vendor_pib="100000001",
    iban="RS35 1600 9999 8888 7777 44", # LAZNI IBAN (Ne poklapa se sa bazom)
    amount="4,500.00",
    invoice_date=today_date,
    is_fraud=True
)

# 3. NOVI PARTNER (Sistem ga još ne poznaje)
generate_invoice(
    filename="3_Novi_Partner.pdf",
    vendor_name="Startup Studio DOO",
    vendor_pib="105555555",
    iban="RS35 1150 1111 2222 3333 44",
    amount="1,200.00",
    invoice_date=today_date,
    is_fraud=False
)

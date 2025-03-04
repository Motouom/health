Features Discussed but Not Implemented
These were brainstormed in our discussion but haven’t been added yet:

Stock Alerts and Reordering:
Notify when stock falls below a threshold (e.g., < 10 units).
Suggest or automate reordering.
Status: Not implemented—requires a minStockLevel field and alert logic.
Expiry Date Tracking:
Track and alert on product expiration dates.
Status: Not implemented—needs expiryDate in Product/InventoryTransaction and UI alerts.
Batch/Lot Number Management:
Track products by batch/lot for recalls or quality control.
Status: Not implemented—requires batchNumber field and UI updates.
Multi-Location Inventory:
Manage stock across multiple hospital locations (e.g., pharmacy, ER).
Status: Not implemented—needs a Location entity and location-based tracking.
Supplier Management:
Maintain a supplier list with details for reordering.
Status: Not implemented—requires a Supplier entity and UI page.
Usage Reports and Analytics:
Detailed reports (e.g., top-used items, trends) beyond sales summary.
Status: Not implemented—needs chart integration (e.g., Chart.js) and new endpoints.
User Roles and Permissions:
Role-based access (admin, staff, auditor) with JWT instead of Basic Auth.
Status: Not implemented—requires User/Role entities and security config.
Barcode/QR Code Integration:
Scan codes to manage products.
Status: Not implemented—needs a barcode library and UI integration.
Audit Trail:
Log all actions (who did what, when) beyond stock transactions.
Status: Not implemented—requires an AuditLog entity and broader logging.
Patient Linking:
Link product exits to patients or departments.
Status: Not implemented—needs Patient/Department entities and UI updates.
Mobile-Friendly UI:
Optimize for mobile devices.
Status: Not implemented—requires responsive CSS tweaks.
Integration with Hospital Systems:
Connect to EHR or pharmacy systems.
Status: Not implemented—requires API standards (e.g., HL7) and integration logic.
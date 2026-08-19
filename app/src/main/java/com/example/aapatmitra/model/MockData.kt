package com.example.aapatmitra.model

object MockData {
    val initialUserProfile = UserProfile(
        name = "Rahul Sharma",
        email = "kandpalu23@gmail.com",
        age = 42,
        gender = "Male",
        phone = "+91 98765 43210",
        householdPhone = "+91 98765 43210",
        bloodGroup = "O+",
        address = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
        allergies = listOf("Penicillin", "Sulfa Drugs", "Peanuts"),
        chronicConditions = listOf("Hypertension", "Mild Asthma"),
        emergencyNotes = "Carries salbutamol inhaler. Prefers Max Hospital Saket or Fortis Memorial.",
        isLoggedIn = false
    )

    val initialInsurances = listOf(
        InsurancePolicy(
            id = "ins-1",
            provider = "Star Health & Allied Insurance",
            policyNumber = "SH-FAMILY-OPTI-992140",
            policyHolder = "Rahul Sharma",
            sumInsured = "₹ 15,00,000",
            expiryDate = "2027-04-30",
            isVerified = true,
            verificationCode = "VRF-STAR-8832-OK",
            verificationDate = "2026-05-12",
            tpaDeskPhone = "1800-425-2255",
            coverageType = "Cashless",
            networkHospitals = listOf("Max Super Speciality Hospital", "Fortis Memorial Research Institute", "Apollo Multi-Speciality", "Medanta The Medicity"),
            notes = "Co-pay: 0%. Pre-existing diseases covered."
        ),
        InsurancePolicy(
            id = "ins-2",
            provider = "HDFC ERGO Health Suraksha",
            policyNumber = "HDFC-ERGO-CRIT-449102",
            policyHolder = "Rahul Sharma",
            sumInsured = "₹ 25,00,000",
            expiryDate = "2027-11-15",
            isVerified = true,
            verificationCode = "VRF-HDFC-9941-OK",
            verificationDate = "2026-06-01",
            tpaDeskPhone = "1800-2666",
            coverageType = "Both",
            networkHospitals = listOf("Max Super Speciality Hospital", "Apollo Multi-Speciality", "AIIMS Emergency Trauma Centre"),
            notes = "Critical illness top-up cover."
        ),
        InsurancePolicy(
            id = "ins-3",
            provider = "Care Health (Religare) Supreme",
            policyNumber = "CARE-MED-551029",
            policyHolder = "Rahul Sharma",
            sumInsured = "₹ 10,00,000",
            expiryDate = "2026-12-31",
            isVerified = false,
            tpaDeskPhone = "1800-102-4455",
            coverageType = "Cashless",
            networkHospitals = listOf("Fortis Memorial Research Institute", "City Care Multi-Speciality Hospital"),
            notes = "Awaiting automatic TPA API verification refresh."
        )
    )

    val initialReports = listOf(
        MedicalReport(
            id = "rep-1",
            title = "Comprehensive Cardiac Evaluation & Echo",
            date = "2026-02-14",
            category = "ECG/Cardiology",
            doctorOrHospital = "Dr. S. Mehta - Max Super Speciality Hospital",
            summary = "Normal sinus rhythm, LVEF 62%, mild trace mitral regurgitation, advised low sodium diet.",
            tags = listOf("Cardiology", "Echo", "BP")
        ),
        MedicalReport(
            id = "rep-2",
            title = "Discharge Summary - Respiratory Episode",
            date = "2025-11-20",
            category = "Discharge Summary",
            doctorOrHospital = "Fortis Memorial Research Institute",
            summary = "Treated for acute bronchospasm, discharged in stable condition on inhaler maintenance.",
            tags = listOf("Pulmonology", "Asthma")
        ),
        MedicalReport(
            id = "rep-3",
            title = "Annual Blood Chemistry & Lipid Profile",
            date = "2026-01-10",
            category = "Lab Report",
            doctorOrHospital = "Apollo Diagnostics Centre",
            summary = "HbA1c: 5.6% (Normal), Total Cholesterol: 195 mg/dL, Creatinine: 0.9 mg/dL (Normal).",
            tags = listOf("Blood Test", "Lipids", "Kidney")
        ),
        MedicalReport(
            id = "rep-4",
            title = "Drug Allergy & Anaphylaxis Record",
            date = "2024-08-05",
            category = "Other",
            doctorOrHospital = "AIIMS Allergy Clinic",
            summary = "Severe urticaria reaction confirmed to Penicillin & Ampicillin derivatives. Sulfa sensitivity noted.",
            tags = listOf("Allergies", "Emergency Red Alert")
        )
    )

    val initialFamilyMembers = listOf(
        FamilyMember(
            id = "fam-1",
            name = "Rahul Sharma",
            relation = "Self",
            age = 42,
            gender = "Male",
            phone = "+91 98765 43210",
            householdAnchorPhone = "+91 98765 43210",
            bloodGroup = "O+",
            address = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
            allergies = listOf("Penicillin", "Sulfa Drugs", "Peanuts"),
            chronicConditions = listOf("Hypertension", "Mild Asthma"),
            emergencyNotes = "Carries salbutamol inhaler. Prefers Max Hospital Saket or Fortis Memorial.",
            primaryDoctorName = "Dr. S. Mehta (Cardiologist)",
            primaryDoctorPhone = "+91 98111 22334",
            isDefaultPatient = true,
            avatarColor = "blue",
            insurances = initialInsurances,
            reports = initialReports
        ),
        FamilyMember(
            id = "fam-2",
            name = "Priya Sharma",
            relation = "Spouse",
            age = 39,
            gender = "Female",
            phone = "+91 98112 34567",
            householdAnchorPhone = "+91 98765 43210",
            bloodGroup = "B+",
            address = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
            allergies = listOf("NSAIDs / Ibuprofen", "Dust Mites"),
            chronicConditions = listOf("Thyroid (Hypothyroidism)", "Migraine"),
            emergencyNotes = "Takes Thyronorm 50mcg daily. Avoid non-steroidal anti-inflammatory drugs.",
            primaryDoctorName = "Dr. Suniti Rao (Endocrinologist)",
            primaryDoctorPhone = "+91 98222 33445",
            isDefaultPatient = false,
            avatarColor = "rose",
            insurances = listOf(initialInsurances[0]),
            reports = listOf(
                MedicalReport(
                    id = "rep-priya-1",
                    title = "Thyroid Profile & CBC",
                    date = "2026-03-10",
                    category = "Lab Report",
                    doctorOrHospital = "Dr. Lal PathLabs, Gurugram",
                    summary = "TSH: 2.4 mIU/L (Euthyroid target reached), Hb: 12.8 g/dL.",
                    tags = listOf("Thyroid", "CBC")
                )
            )
        ),
        FamilyMember(
            id = "fam-3",
            name = "Vikram Sharma",
            relation = "Father",
            age = 71,
            gender = "Male",
            phone = "+91 98334 56789",
            householdAnchorPhone = "+91 98765 43210",
            bloodGroup = "AB+",
            address = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
            allergies = listOf("Aspirin", "Contrast Dye (Iodine)"),
            chronicConditions = listOf("Type 2 Diabetes Mellitus", "Coronary Artery Disease (CAD)", "Hypertension"),
            emergencyNotes = "⚠️ High Risk Cardiac Patient. Drug-eluting stent placed in 2021 (LAD). Takes Clopidogrel & Metformin. Stretcher access required.",
            primaryDoctorName = "Dr. K. Trehan (Senior Cardiologist)",
            primaryDoctorPhone = "+91 98333 44556",
            isDefaultPatient = false,
            avatarColor = "amber",
            insurances = listOf(
                InsurancePolicy(
                    id = "ins-vikram-1",
                    provider = "Niva Bupa Senior First Gold",
                    policyNumber = "NB-SR-GOLD-882910",
                    policyHolder = "Vikram Sharma",
                    sumInsured = "₹ 20,00,000",
                    expiryDate = "2027-08-31",
                    isVerified = true,
                    verificationCode = "VRF-NIVA-VIKRAM-7729",
                    tpaDeskPhone = "1800-419-7878",
                    coverageType = "Both",
                    networkHospitals = listOf("Max Super Speciality Hospital", "Medanta The Medicity", "Fortis Memorial", "Apollo Multi-Speciality"),
                    notes = "Senior Citizen Cashless Priority Desk activated. 0% co-pay."
                )
            ),
            reports = listOf(
                MedicalReport(
                    id = "rep-vikram-1",
                    title = "Coronary Angiography Follow-Up & Holter",
                    date = "2026-01-20",
                    category = "ECG/Cardiology",
                    doctorOrHospital = "Medanta The Medicity, Gurugram",
                    summary = "Patent stent in LAD, LVEF 55%, no malignant arrhythmias detected during 24h Holter.",
                    tags = listOf("Cardiology", "Stent", "Angio")
                )
            )
        ),
        FamilyMember(
            id = "fam-4",
            name = "Ananya Sharma",
            relation = "Daughter",
            age = 12,
            gender = "Female",
            phone = "+91 98445 67890",
            householdAnchorPhone = "+91 98765 43210",
            bloodGroup = "O+",
            address = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
            allergies = listOf("Peanuts / Tree Nuts (Severe Anaphylaxis Risk)"),
            chronicConditions = listOf("Pediatric Food Allergy"),
            emergencyNotes = "⚠️ SEVERE NUT ALLERGY. Carries pediatric EpiPen (0.15mg). In case of anaphylaxis administer immediately in outer thigh.",
            primaryDoctorName = "Dr. Meera Kapoor (Pediatrician)",
            primaryDoctorPhone = "+91 98555 66778",
            isDefaultPatient = false,
            avatarColor = "purple",
            insurances = listOf(initialInsurances[0]),
            reports = listOf(
                MedicalReport(
                    id = "rep-ananya-1",
                    title = "Pediatric Allergy Skin Prick & IgE",
                    date = "2025-09-15",
                    category = "Lab Report",
                    doctorOrHospital = "Fortis Pediatric Allergy Center",
                    summary = "High IgE specific to Peanut >100 kU/L. Epinephrine prescribed for emergency protocol.",
                    tags = listOf("Pediatric", "Allergy", "EpiPen")
                )
            )
        )
    )

    val initialAngelContacts = listOf(
        AngelContact(
            id = "angel-1",
            name = "Priya Sharma",
            relation = "Spouse",
            phone = "+91 98112 34567",
            notifyOnEmergency = true,
            lastAlertStatus = "idle"
        ),
        AngelContact(
            id = "angel-2",
            name = "Dr. Alok Verma",
            relation = "Family Physician / Brother",
            phone = "+91 98223 45678",
            notifyOnEmergency = true,
            lastAlertStatus = "idle"
        ),
        AngelContact(
            id = "angel-3",
            name = "Vikram Sharma",
            relation = "Father",
            phone = "+91 98334 56789",
            notifyOnEmergency = true,
            lastAlertStatus = "idle"
        )
    )

    val hospitals = listOf(
        Hospital(
            id = "hosp-max",
            name = "Max Super Speciality Hospital",
            address = "Sector 43, Sushant Lok Phase I, Gurugram",
            distanceKm = 2.8,
            emergencyPhone = "0124-6623000",
            traumaLevel = "Level 1 Trauma",
            availableEmergencyBeds = 14,
            icuBedsAvailable = 6,
            rating = 4.8,
            ambulancesOwned = listOf("amb-max-1", "amb-max-2"),
            specialties = listOf("Cardiac Emergency", "Trauma & Ortho", "Stroke Rapid Response", "Critical Care"),
            lat = 28.4595,
            lng = 77.0725,
            acceptsInsurance = listOf("Star Health", "HDFC ERGO", "Care Health", "ICICI Lombard")
        ),
        Hospital(
            id = "hosp-fortis",
            name = "Fortis Memorial Research Institute (FMRI)",
            address = "Sector 44, Opposite HUDA City Centre, Gurugram",
            distanceKm = 3.4,
            emergencyPhone = "0124-4962200",
            traumaLevel = "Level 1 Trauma",
            availableEmergencyBeds = 18,
            icuBedsAvailable = 8,
            rating = 4.9,
            ambulancesOwned = listOf("amb-fortis-1"),
            specialties = listOf("Neuro Critical Care", "Cardiology", "Polytrauma", "Pediatric Emergency"),
            lat = 28.4632,
            lng = 77.0688,
            acceptsInsurance = listOf("Star Health", "HDFC ERGO", "Care Health", "Bajaj Allianz")
        ),
        Hospital(
            id = "hosp-apollo",
            name = "Apollo Multi-Speciality Hospital",
            address = "DLF Phase 2, MG Road, Gurugram",
            distanceKm = 4.6,
            emergencyPhone = "0124-7101111",
            traumaLevel = "Cardiac & Neuro Center",
            availableEmergencyBeds = 11,
            icuBedsAvailable = 4,
            rating = 4.7,
            ambulancesOwned = listOf("amb-apollo-1"),
            specialties = listOf("Emergency Coronary Care", "Toxicology", "Pulmonary Care"),
            lat = 28.4789,
            lng = 77.0872,
            acceptsInsurance = listOf("Star Health", "HDFC ERGO", "ICICI Lombard")
        ),
        Hospital(
            id = "hosp-aiims",
            name = "AIIMS Emergency Trauma Centre (Apex)",
            address = "Ring Road, Safdarjung Enclave, New Delhi",
            distanceKm = 14.2,
            emergencyPhone = "011-26593677",
            traumaLevel = "Level 1 Trauma",
            availableEmergencyBeds = 32,
            icuBedsAvailable = 15,
            rating = 4.9,
            ambulancesOwned = listOf("amb-108-universal-1"),
            specialties = listOf("Apex Poly-Trauma", "Burn Care", "Neurosurgery", "Organ Transplant Emergency"),
            lat = 28.5672,
            lng = 77.2100,
            acceptsInsurance = listOf("All Insurances", "Ayushman Bharat / PMJAY", "CGHS", "ECHS")
        ),
        Hospital(
            id = "hosp-city",
            name = "City Care Multi-Speciality Hospital",
            address = "Sector 56 Market Road, Gurugram",
            distanceKm = 1.6,
            emergencyPhone = "0124-4228899",
            traumaLevel = "Level 2",
            availableEmergencyBeds = 7,
            icuBedsAvailable = 3,
            rating = 4.4,
            ambulancesOwned = emptyList(),
            specialties = listOf("General Emergency", "Fracture Care", "Acute Internal Medicine"),
            lat = 28.4320,
            lng = 77.1040,
            acceptsInsurance = listOf("Star Health", "Care Health")
        )
    )

    val ambulances = listOf(
        Ambulance(
            id = "amb-max-1",
            name = "Max Healthcare Advanced Cardiac Life Support (ALS)",
            driverName = "Sanjay Yadav (Paramedic Lead)",
            driverPhone = "+91 98111 00234",
            vehicleNo = "HR-26-MB-4412",
            type = "ALS",
            hospitalAffiliation = "Max Super Speciality Hospital",
            isPrivateHospitalOwned = true,
            rating = 4.95,
            etaMinutes = 4,
            distanceKm = 1.8,
            currentLocationName = "Sector 43 Red Light / Golf Course Jn",
            lat = 28.4550,
            lng = 77.0810,
            equipment = listOf("Ventilator", "Defibrillator / AED", "Cardiac Monitor", "Oxygen Cylinder", "Spine Board", "Suction Unit", "IV Infusion"),
            status = "available"
        ),
        Ambulance(
            id = "amb-max-2",
            name = "Max Critical Care Rapid Unit 02",
            driverName = "Ramesh Chander",
            driverPhone = "+91 98111 00235",
            vehicleNo = "HR-26-MB-8891",
            type = "ICU",
            hospitalAffiliation = "Max Super Speciality Hospital",
            isPrivateHospitalOwned = true,
            rating = 4.88,
            etaMinutes = 6,
            distanceKm = 2.9,
            currentLocationName = "Sushant Lok Phase 1 Gate 3",
            lat = 28.4610,
            lng = 77.0750,
            equipment = listOf("ICU Ventilator", "Multi-param Monitor", "Syringe Pumps", "Emergency Drug Kit"),
            status = "available"
        ),
        Ambulance(
            id = "amb-fortis-1",
            name = "Fortis Rescue ALS & Mobile Trauma Unit",
            driverName = "Kuldeep Singh (Senior EMT)",
            driverPhone = "+91 98222 11988",
            vehicleNo = "HR-26-FM-9021",
            type = "ALS",
            hospitalAffiliation = "Fortis Memorial Research Institute (FMRI)",
            isPrivateHospitalOwned = true,
            rating = 4.92,
            etaMinutes = 5,
            distanceKm = 2.3,
            currentLocationName = "Sector 44 Fortis Hub Standby",
            lat = 28.4640,
            lng = 77.0695,
            equipment = listOf("Transport Ventilator", "Lucas CPR Machine", "Defibrillator", "Cold Chain Box"),
            status = "available"
        ),
        Ambulance(
            id = "amb-apollo-1",
            name = "Apollo 1066 Rapid ALS Response",
            driverName = "Deepak Narang",
            driverPhone = "+91 98333 44556",
            vehicleNo = "DL-01-AP-6677",
            type = "ALS",
            hospitalAffiliation = "Apollo Multi-Speciality Hospital",
            isPrivateHospitalOwned = true,
            rating = 4.85,
            etaMinutes = 8,
            distanceKm = 4.1,
            currentLocationName = "MG Road Metro Station Pillar 128",
            lat = 28.4800,
            lng = 77.0850,
            equipment = listOf("LifePak 15 Monitor", "Oxygen Stretcher", "Intubation Kit"),
            status = "available"
        ),
        Ambulance(
            id = "amb-universal-1",
            name = "MedPulse 24x7 Universal ALS Ambulance",
            driverName = "Amit Bhardwaj",
            driverPhone = "+91 98710 99012",
            vehicleNo = "HR-55-AA-3321",
            type = "ALS",
            isPrivateHospitalOwned = false,
            rating = 4.78,
            etaMinutes = 7,
            distanceKm = 3.2,
            currentLocationName = "Sector 56 Huda Market Stand",
            lat = 28.4315,
            lng = 77.1020,
            equipment = listOf("AED", "Oxygen System", "First Aid Trauma Kit", "Stretcher"),
            status = "available"
        ),
        Ambulance(
            id = "amb-108-universal-1",
            name = "National 108 Emergency Response ALS",
            driverName = "Virender Kumar (Govt EMT)",
            driverPhone = "+91 98990 00108",
            vehicleNo = "HR-26-G-1088",
            type = "ALS",
            isPrivateHospitalOwned = false,
            rating = 4.70,
            etaMinutes = 9,
            distanceKm = 4.5,
            currentLocationName = "Iffco Chowk Flyover Junction",
            lat = 28.4720,
            lng = 77.0580,
            equipment = listOf("Standard ALS Kit", "Defibrillator", "Oxygen Delivery", "Scoop Stretcher"),
            status = "available"
        ),
        Ambulance(
            id = "amb-backup-medilink",
            name = "MediLink Standby Backup ICU Van",
            driverName = "Suraj Rawat (Emergency Driver)",
            driverPhone = "+91 98777 55432",
            vehicleNo = "HR-26-BK-9900",
            type = "ICU",
            isPrivateHospitalOwned = false,
            rating = 4.89,
            etaMinutes = 3,
            distanceKm = 1.2,
            currentLocationName = "Golf Course Road Underpass Fast-Lane",
            lat = 28.4480,
            lng = 77.0910,
            equipment = listOf("Backup Critical ICU kit", "Dual Oxygen", "Emergency Siren Paging"),
            status = "available"
        )
    )

    val initialNonEmergencyBookings = listOf(
        NonEmergencyBooking(
            id = "neb-1",
            patientName = "Vikram Sharma",
            phone = "+91 98334 56789",
            serviceType = "Wheelchair Van",
            pickupAddress = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
            dropAddress = "Medanta The Medicity, Sector 38, Gurugram",
            scheduledDate = "Tomorrow, 10:30 AM",
            scheduledTime = "10:30 AM",
            specialAssistance = listOf("Wheelchair Ramp Support", "Oxygen Concentrator"),
            status = "confirmed"
        )
    )
}

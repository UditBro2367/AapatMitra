package com.example.aapatmitra.model

data class SearchRadiusTier(
    val tier: Int,
    val radiusKm: Double,
    val waitTimeSeconds: Int,
    val label: String
)

data class DispatchSearchResult(
    val ambulance: Ambulance?,
    val tier: Int,
    val isHospitalAffiliatedMatch: Boolean,
    val searchRadiusKm: Double
)

object DispatchAlgorithm {
    val searchRadiusTiers = listOf(
        SearchRadiusTier(1, 2.5, 60, "Immediate Vicinity (2.5 km) - Prioritizing Hospital Fleet"),
        SearchRadiusTier(2, 5.0, 60, "Mid-Zone Radius (5.0 km) - Scanning Regional Multi-Speciality & Universal ALS"),
        SearchRadiusTier(3, 12.0, 60, "Wide Metro Grid (12.0 km) - Activating Apex Emergency Reserve Units")
    )

    fun findBestAmbulance(
        targetHospital: Hospital,
        currentRadiusKm: Double,
        availableAmbulances: List<Ambulance>,
        excludedAmbulanceIds: List<String> = emptyList()
    ): DispatchSearchResult {
        val eligible = availableAmbulances.filter {
            !excludedAmbulanceIds.contains(it.id) &&
                    it.status != "stalled" &&
                    it.distanceKm <= currentRadiusKm
        }.toMutableList()

        if (eligible.isEmpty()) {
            return DispatchSearchResult(
                ambulance = null,
                tier = if (currentRadiusKm <= 2.5) 1 else if (currentRadiusKm <= 5.0) 2 else 3,
                isHospitalAffiliatedMatch = false,
                searchRadiusKm = currentRadiusKm
            )
        }

        eligible.sortWith { a, b ->
            val aIsDirect = a.hospitalAffiliation == targetHospital.name
            val bIsDirect = b.hospitalAffiliation == targetHospital.name

            if (aIsDirect && !bIsDirect) -1
            else if (!aIsDirect && bIsDirect) 1
            else if (a.isPrivateHospitalOwned && !b.isPrivateHospitalOwned) -1
            else if (!a.isPrivateHospitalOwned && b.isPrivateHospitalOwned) 1
            else {
                val etaCmp = a.etaMinutes.compareTo(b.etaMinutes)
                if (etaCmp != 0) etaCmp else a.distanceKm.compareTo(b.distanceKm)
            }
        }

        val best = eligible.first()
        val isDirect = best.hospitalAffiliation == targetHospital.name

        return DispatchSearchResult(
            ambulance = best,
            tier = if (currentRadiusKm <= 2.5) 1 else if (currentRadiusKm <= 5.0) 2 else 3,
            isHospitalAffiliatedMatch = isDirect,
            searchRadiusKm = currentRadiusKm
        )
    }
}

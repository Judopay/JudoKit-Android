package com.judopay.judokit.android.model

data class AdministrativeDivision(
    val isoCode: String,
    val name: String,
) {
    override fun toString() = this.name
}

val american =
    listOf(
        AdministrativeDivision(isoCode = "AK", name = "Alaska"),
        AdministrativeDivision(isoCode = "AZ", name = "Arizona"),
        AdministrativeDivision(isoCode = "AR", name = "Arkansas"),
        AdministrativeDivision(isoCode = "CA", name = "California"),
        AdministrativeDivision(isoCode = "CO", name = "Colorado"),
        AdministrativeDivision(isoCode = "CT", name = "Connecticut"),
        AdministrativeDivision(isoCode = "DE", name = "Delaware"),
        AdministrativeDivision(isoCode = "AL", name = "Alabama"),
        AdministrativeDivision(isoCode = "DC", name = "District Of Columbia"),
        AdministrativeDivision(isoCode = "FL", name = "Florida"),
        AdministrativeDivision(isoCode = "GA", name = "Georgia"),
        AdministrativeDivision(isoCode = "HI", name = "Hawaii"),
        AdministrativeDivision(isoCode = "ID", name = "Idaho"),
        AdministrativeDivision(isoCode = "IL", name = "Illinois"),
        AdministrativeDivision(isoCode = "IN", name = "Indiana"),
        AdministrativeDivision(isoCode = "IA", name = "Iowa"),
        AdministrativeDivision(isoCode = "KS", name = "Kansas"),
        AdministrativeDivision(isoCode = "KY", name = "Kentucky"),
        AdministrativeDivision(isoCode = "LA", name = "Louisiana"),
        AdministrativeDivision(isoCode = "ME", name = "Maine"),
        AdministrativeDivision(isoCode = "MD", name = "Maryland"),
        AdministrativeDivision(isoCode = "MA", name = "Massachusetts"),
        AdministrativeDivision(isoCode = "MI", name = "Michigan"),
        AdministrativeDivision(isoCode = "MN", name = "Minnesota"),
        AdministrativeDivision(isoCode = "MS", name = "Mississippi"),
        AdministrativeDivision(isoCode = "MO", name = "Missouri"),
        AdministrativeDivision(isoCode = "MT", name = "Montana"),
        AdministrativeDivision(isoCode = "NE", name = "Nebraska"),
        AdministrativeDivision(isoCode = "NV", name = "Nevada"),
        AdministrativeDivision(isoCode = "NH", name = "New Hampshire"),
        AdministrativeDivision(isoCode = "NJ", name = "New Jersey"),
        AdministrativeDivision(isoCode = "NM", name = "New Mexico"),
        AdministrativeDivision(isoCode = "NY", name = "New York"),
        AdministrativeDivision(isoCode = "NC", name = "North Carolina"),
        AdministrativeDivision(isoCode = "ND", name = "North Dakota"),
        AdministrativeDivision(isoCode = "OH", name = "Ohio"),
        AdministrativeDivision(isoCode = "OK", name = "Oklahoma"),
        AdministrativeDivision(isoCode = "OR", name = "Oregon"),
        AdministrativeDivision(isoCode = "PA", name = "Pennsylvania"),
        AdministrativeDivision(isoCode = "RI", name = "Rhode Island"),
        AdministrativeDivision(isoCode = "SC", name = "South Carolina"),
        AdministrativeDivision(isoCode = "SD", name = "South Dakota"),
        AdministrativeDivision(isoCode = "TN", name = "Tennessee"),
        AdministrativeDivision(isoCode = "TX", name = "Texas"),
        AdministrativeDivision(isoCode = "UT", name = "Utah"),
        AdministrativeDivision(isoCode = "VT", name = "Vermont"),
        AdministrativeDivision(isoCode = "VA", name = "Virginia"),
        AdministrativeDivision(isoCode = "WA", name = "Washington"),
        AdministrativeDivision(isoCode = "WV", name = "West Virginia"),
        AdministrativeDivision(isoCode = "WI", name = "Wisconsin"),
        AdministrativeDivision(isoCode = "WY", name = "Wyoming"),
    )

val canadian =
    listOf(
        AdministrativeDivision(isoCode = "AB", name = "Alberta"),
        AdministrativeDivision(isoCode = "BC", name = "British Columbia"),
        AdministrativeDivision(isoCode = "MB", name = "Manitoba"),
        AdministrativeDivision(isoCode = "NB", name = "New Brunswick"),
        AdministrativeDivision(isoCode = "NL", name = "Newfoundland and Labrador"),
        AdministrativeDivision(isoCode = "NS", name = "Nova Scotia"),
        AdministrativeDivision(isoCode = "NT", name = "Northwest Territories"),
        AdministrativeDivision(isoCode = "NU", name = "Nunavut"),
        AdministrativeDivision(isoCode = "ON", name = "Ontario"),
        AdministrativeDivision(isoCode = "PE", name = "Prince Edward Island"),
        AdministrativeDivision(isoCode = "QC", name = "Quebec"),
        AdministrativeDivision(isoCode = "SK", name = "Saskatchewan"),
        AdministrativeDivision(isoCode = "YT", name = "Yukon"),
    )

val indian =
    listOf(
        AdministrativeDivision(isoCode = "AN", name = "Andaman and Nicobar Islands"),
        AdministrativeDivision(isoCode = "AP", name = "Andhra Pradesh"),
        AdministrativeDivision(isoCode = "AR", name = "Arunachal Pradesh"),
        AdministrativeDivision(isoCode = "AS", name = "Assam"),
        AdministrativeDivision(isoCode = "BR", name = "Bihar"),
        AdministrativeDivision(isoCode = "CH", name = "Chandigarh"),
        AdministrativeDivision(isoCode = "CG", name = "Chhattisgarh"),
        AdministrativeDivision(isoCode = "DL", name = "Delhi"),
        AdministrativeDivision(isoCode = "DH", name = "Dadra and Nagar Haveli and Daman and Diu"),
        AdministrativeDivision(isoCode = "GA", name = "Goa"),
        AdministrativeDivision(isoCode = "GJ", name = "Gujarat"),
        AdministrativeDivision(isoCode = "HR", name = "Haryana"),
        AdministrativeDivision(isoCode = "HP", name = "Himachal Pradesh"),
        AdministrativeDivision(isoCode = "JK", name = "Jammu and Kashmīr"),
        AdministrativeDivision(isoCode = "JH", name = "Jharkhand"),
        AdministrativeDivision(isoCode = "KA", name = "Karnataka"),
        AdministrativeDivision(isoCode = "KL", name = "Kerala"),
        AdministrativeDivision(isoCode = "LA", name = "Ladakh"),
        AdministrativeDivision(isoCode = "LD", name = "Lakshadweep"),
        AdministrativeDivision(isoCode = "MP", name = "Madhya Pradesh"),
        AdministrativeDivision(isoCode = "MH", name = "Maharashtra"),
        AdministrativeDivision(isoCode = "MN", name = "Manipur"),
        AdministrativeDivision(isoCode = "ML", name = "Meghalaya"),
        AdministrativeDivision(isoCode = "MZ", name = "Mizoram"),
        AdministrativeDivision(isoCode = "NL", name = "Nāgāland"),
        AdministrativeDivision(isoCode = "OD", name = "Odisha"),
        AdministrativeDivision(isoCode = "PY", name = "Puducherry"),
        AdministrativeDivision(isoCode = "PB", name = "Punjab"),
        AdministrativeDivision(isoCode = "RJ", name = "Rajasthan"),
        AdministrativeDivision(isoCode = "SK", name = "Sikkim"),
        AdministrativeDivision(isoCode = "TN", name = "Tamil Nadu"),
        AdministrativeDivision(isoCode = "TS", name = "Telangana"),
        AdministrativeDivision(isoCode = "TR", name = "Tripura"),
        AdministrativeDivision(isoCode = "UP", name = "Uttar Pradesh"),
        AdministrativeDivision(isoCode = "UK", name = "Uttarakhand"),
        AdministrativeDivision(isoCode = "WB", name = "West Bengal"),
    )

val chinese =
    listOf(
        AdministrativeDivision(isoCode = "AH", name = "Anhui Sheng"),
        AdministrativeDivision(isoCode = "MO", name = "Aomen Tebiexingzhengqu"),
        AdministrativeDivision(isoCode = "BJ", name = "Beijing Shi"),
        AdministrativeDivision(isoCode = "CQ", name = "Chongqing Shi"),
        AdministrativeDivision(isoCode = "FJ", name = "Fujian Sheng"),
        AdministrativeDivision(isoCode = "GS", name = "Gansu Sheng"),
        AdministrativeDivision(isoCode = "GD", name = "Guangdong Sheng"),
        AdministrativeDivision(isoCode = "GX", name = "Guangxi Zhuangzu Zizhiqu"),
        AdministrativeDivision(isoCode = "GZ", name = "Guizhou Sheng"),
        AdministrativeDivision(isoCode = "HI", name = "Hainan Sheng"),
        AdministrativeDivision(isoCode = "HE", name = "Hebei Sheng"),
        AdministrativeDivision(isoCode = "HL", name = "Heilongjiang Sheng"),
        AdministrativeDivision(isoCode = "HA", name = "Henan Sheng"),
        AdministrativeDivision(isoCode = "HK", name = "Hong Kong SAR"),
        AdministrativeDivision(isoCode = "HB", name = "Hubei Sheng"),
        AdministrativeDivision(isoCode = "HN", name = "Hunan Sheng"),
        AdministrativeDivision(isoCode = "JS", name = "Jiangsu Sheng"),
        AdministrativeDivision(isoCode = "JX", name = "Jiangxi Sheng"),
        AdministrativeDivision(isoCode = "JL", name = "Jilin Sheng"),
        AdministrativeDivision(isoCode = "LN", name = "Liaoning Sheng"),
        AdministrativeDivision(isoCode = "MO", name = "Macao SAR"),
        AdministrativeDivision(isoCode = "MO", name = "Macau SAR"),
        AdministrativeDivision(isoCode = "NM", name = "Nei Mongol Zizhiqu"),
        AdministrativeDivision(isoCode = "NX", name = "Ningxia Huizu Zizhiqu"),
        AdministrativeDivision(isoCode = "QH", name = "Qinghai Sheng"),
        AdministrativeDivision(isoCode = "SN", name = "Shaanxi Sheng"),
        AdministrativeDivision(isoCode = "SD", name = "Shandong Sheng"),
        AdministrativeDivision(isoCode = "SH", name = "Shanghai Shi"),
        AdministrativeDivision(isoCode = "SX", name = "Shanxi Sheng"),
        AdministrativeDivision(isoCode = "SC", name = "Sichuan Sheng"),
        AdministrativeDivision(isoCode = "TW", name = "Taiwan Sheng"),
        AdministrativeDivision(isoCode = "TJ", name = "Tianjin Shi"),
        AdministrativeDivision(isoCode = "HK", name = "Xianggang Tebiexingzhengqu"),
        AdministrativeDivision(isoCode = "XJ", name = "Xinjiang Uygur Zizhiqu"),
        AdministrativeDivision(isoCode = "XZ", name = "Xizang Zizhiqu"),
        AdministrativeDivision(isoCode = "YN", name = "Yunnan Sheng"),
        AdministrativeDivision(isoCode = "ZJ", name = "Zhejiang Sheng"),
    )

package io.github.ifa.glancewidget.model.premium

data class PremiumBillingProduct(
    val productId: String,
    val productType: ProductType,
    val planType: PlanType,
    val billingPeriod: BillingPeriod,
    val formattedPrice: String,
) {
    enum class ProductType {
        Subscription, InApp, Unknown
    }
    enum class PlanType {
        Standard, Premium, Unknown
    }
    enum class BillingPeriod {
        Yearly, Monthly, Once
    }
}

#  Shopwise — Modüler Monolith E-Ticaret API

Shopwise, Spring Boot ile geliştirilmiş, gerçek dünya projelerinde kullanılan mimari pattern'leri içeren bir **Modüler Monolith** e-ticaret API'sidir.

---

##  Mimari

Bu proje, mikroservise geçişe hazır, modüler bir monolith mimarisi üzerine inşa edilmiştir. Her modül kendi `domain`, `application`, `infrastructure` ve `api` katmanlarına sahiptir. Modüller arası iletişim **Interface/Port Pattern** ve **Event Driven** yaklaşımıyla sağlanmıştır.

```
com.shopwise/
├── shared/          → Ortak kodlar
├── user/            → Kullanıcı yönetimi
├── product/         → Ürün kataloğu
└── order/           → Sipariş yönetimi

```

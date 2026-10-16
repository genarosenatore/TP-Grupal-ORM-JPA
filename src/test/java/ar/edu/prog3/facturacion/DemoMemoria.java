package ar.edu.prog3.facturacion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;
import ar.edu.prog3.facturacion.modelo.FacturaVenta;

/** Alternativa de demostracion sin instalar PostgreSQL. Solo existe en el classpath de pruebas. */
public final class DemoMemoria {
    private DemoMemoria() { }

    public static void main(String[] args) {
        Map<String, Object> propiedades = Map.of(
                "javax.persistence.jdbc.driver", "org.h2.Driver",
                "javax.persistence.jdbc.url", "jdbc:h2:mem:demostracion;MODE=PostgreSQL",
                "javax.persistence.jdbc.user", "sa",
                "javax.persistence.jdbc.password", "",
                "hibernate.dialect", "org.hibernate.dialect.H2Dialect",
                "hibernate.hbm2ddl.auto", "create-drop");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU", propiedades);
        try {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                FacturaVenta factura = DatosEjemplo.crearFactura();
                em.persist(factura);
                em.getTransaction().commit();
                em.clear();
                em.getTransaction().begin();
                FacturaVenta recuperada = em.find(FacturaVenta.class, factura.getId());
                if (recuperada.getDetalles().size() != 2) {
                    throw new IllegalStateException("No se recuperaron los detalles");
                }
                System.out.printf("DEMO H2: factura ID=%d, detalles=%d, total=%.2f%n",
                        recuperada.getId(), recuperada.getDetalles().size(), recuperada.getImporteTotal());
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        } finally {
            emf.close();
        }
    }
}

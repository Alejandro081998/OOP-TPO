package personas;

import java.util.ArrayList;

import comercial.*;
import comercial.articulos.ArticuloExtra;
import comercial.articulos.Costo;
import empresa.Empresa;
import excepciones.ServicioException;
import excepciones.ValorException;

public class Administrativo extends Interno {

  public Administrativo(String nombre, long dni, String direccion, String telefono, String contrasena) {
    super(nombre, dni, direccion, telefono, contrasena);
  }

  // ALTERNATIVO SIN DATOS
  public Administrativo(String nombre, String contrasena) {
    super(nombre, contrasena);
  }

  @Override
  public String toString() {
    return "Administrativo [legajo=" + legajo + ", nombre=" + nombre + ", dni=" + dni + ", direccion=" + direccion
        + ", telefono=" + telefono + "]";
  }

  // Metodos para SERVICIOS
  public Servicio verServicio(int nroServicio) {
    return Empresa.getInstance().getServicios(nroServicio);
  }

  public Servicio verServicio(Servicio s) {
    return Empresa.getInstance().getServicios(s);
  }

  public ArticuloExtra crearArticuloExtra(String descripcion, double costo) throws Exception {
    if (0 > costo) {
      throw new ValorException("El costo del articulo extra no puede ser negativo");
    }

    return new ArticuloExtra(descripcion, costo);
  }

  public void agregarArticuloExtraServicio(ArticuloExtra ae, int cantidad, Servicio s) throws Exception {
    if (s.getEstadoServicio() != EstadoServicio.FINALIZADO) {
      throw new ServicioException("El estado del servicio debe estar finalizado por el tecnico");
    }
    if (0 >= cantidad) {
      throw new ServicioException("La cantidad del articulo extra debe ser mayor a 0");
    }
    s.anadirOtroCostos(ae, cantidad);
  }

  public double getGananciaFactura(Factura f) {
    return f.calcularGanancias();
  }

  public double getMargenFactura(Factura f) {
    return f.calcularMargen() * 100;
  }

  public String getMargenFacturaString(Factura f) {
    return "" + Empresa.getInstance().redondear(getMargenFactura(f)) + "%";
  }

  public ArrayList<Servicio> getServiciosAFacturar() {
    ArrayList<Servicio> listaServicios = Empresa.getInstance().getServicios();
    ArrayList<Servicio> listaSinFacturar = new ArrayList<Servicio>();

    for (int i = 0; i < listaServicios.size(); i++) {
      Servicio currentServicio = listaServicios.get(i);
      boolean esServicioFinalizado = currentServicio.getEstadoServicio() == EstadoServicio.FINALIZADO;
      boolean esServicioFacturado = currentServicio.isFacturado();

      if (esServicioFinalizado && esServicioFacturado == false) {
        listaSinFacturar.add(currentServicio);
      }
    }
    return listaSinFacturar;
  }

  public Factura facturarServicio(Servicio s) throws Exception {
    Servicio aFacturar = Empresa.getInstance().getServicios(s);
    Factura nuevaFactura = null;

    if (aFacturar == null) {
      throw new ServicioException("Servicio invalido");
    }

    boolean exitoAlFacturar = aFacturar.facturarServicio();

    if (exitoAlFacturar) {
      nuevaFactura = new Factura(aFacturar);
    } else {
      throw new ServicioException("Error desconocido al facturar");
    }
    return nuevaFactura;

  }

  // Metodos para FACTURAS (propios de administrativo)
  public Factura verFacturaEmpresa(int nroFactura) {
    return Empresa.getInstance().getFacturas(nroFactura);
  }

}

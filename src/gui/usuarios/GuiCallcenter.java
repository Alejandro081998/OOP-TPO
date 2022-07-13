package gui.usuarios;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import agenda.*;
import comercial.articulos.*;
import comercial.Servicio;
import comercial.TipoServicio;
import excepciones.*;
import main.DateAux;
import personas.*;

@SuppressWarnings("serial")
public class GuiCallcenter extends GuiUsuarioBase {
	private final Callcenter CALLCENTER;
	private JPanel panel;
	private JPanel subPanel;
	private JPanel subSubPanel;
	private JPanel pOpcionesCC;
	private JButton btnNewServicio;
	private JButton btnGestionServicios;
	private JButton btnGestionArticulo;
	private JLabel label;
	private JButton btnCrearCliente;

	public GuiCallcenter(Callcenter callcenter) {
		super("Bienvenido " + callcenter.getNombre() + "<CALLCENTER>");
		this.CALLCENTER = callcenter;

		panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 3, 0, 0));

		subPanel = new JPanel();
		panel.add(subPanel);

		subSubPanel = new JPanel();
		panel.add(subSubPanel);
		subSubPanel.setLayout(new GridLayout(3, 0, 0, 0));

		subSubPanel.add(new JLabel(""));

		pOpcionesCC = new JPanel();
		subSubPanel.add(pOpcionesCC);
		pOpcionesCC.setLayout(new GridLayout(0, 1, 0, 0));

		btnNewServicio = new JButton("Crear servicio para cliente");
		btnNewServicio.addActionListener(this);
		pOpcionesCC.add(btnNewServicio);
		// Estetico
		pOpcionesCC.add(new JLabel(""));

		btnGestionServicios = new JButton("Gestionar servicios callcenter");
		btnGestionServicios.addActionListener(this);
		pOpcionesCC.add(btnGestionServicios);
		// Estetico
		pOpcionesCC.add(new JLabel(""));

		btnGestionArticulo = new JButton("Gestionar articulos");
		btnGestionArticulo.addActionListener(this);
		pOpcionesCC.add(btnGestionArticulo);

		label = new JLabel("");
		pOpcionesCC.add(label);

		btnCrearCliente = new JButton("Crear cliente");
		btnCrearCliente.addActionListener(this);
		pOpcionesCC.add(btnCrearCliente);
	}

	private Tecnico seleccionTecnicoDisponible(Servicio s) throws Exception {
		ArrayList<Tecnico> tecnicos = CALLCENTER.getTecnicosDisponibles(s);

		if (tecnicos.size() == 0) {
			throw new AsignacionException("No hay tecnicos disponibles para el servicio seleccionado");
		}

		String opciones = "Seleccione por numero de legajo:";
		for (Tecnico t : tecnicos) {
			opciones += "\n\t" + t.getLegajo() + ") Nombre: " + t.getNombre() + " Seniority: " + t.getSeniority();
		}

		int legajoSeleccionado = guiValidarInt(opciones);
		Tecnico seleccionCliente = CALLCENTER.getTecnicos(legajoSeleccionado, s);

		if (seleccionCliente == null) {
			throw new ValorException("Ingreso una opcion no valida");
		}
		return seleccionCliente;
	}

	private Cliente seleccionClienteDisponible() throws Exception {
		ArrayList<Cliente> clientes = CALLCENTER.getClientesSinServicios();
		ArrayList<Cliente> clientesActivos = CALLCENTER.getClientesActivos();
		boolean existeCliente = false;
		Cliente seleccionCliente = null;
		// String opciones = "Seleccione por numero de cliente:";

		/*
		 * for (Cliente c : clientes) { opciones += "\n\t" + c.getNro() + ") Nombre: " +
		 * c.getNombre() + " DNI: " + c.getDni(); }
		 */

		int nroSeleccionado = guiValidarInt("Ingrese DNI de cliente:");

		for (Cliente c : clientesActivos) {
			if (c.getDni() == nroSeleccionado) {
				seleccionCliente = CALLCENTER.getClientesDNI(nroSeleccionado);
				existeCliente = true;
			}

		}

		if (!existeCliente) {
			seleccionCliente = null;
			throw new ValorException("El cliente no existe, debe crearlo");
		}

		if (existeCliente) {
			
			seleccionCliente = null;
			
			for(Cliente c : clientes) {
				if(c.getDni() == nroSeleccionado) 
					seleccionCliente = CALLCENTER.getClientesDNI(nroSeleccionado);
			}
			
			if(seleccionCliente == null)
				throw new AsignacionException("El cliente tiene servicios pendientes");
		}

		/*
		 * if (seleccionCliente == null || clientes.contains(seleccionCliente) == false)
		 * { throw new ValorException("Ingreso una opcion no valida"); }
		 */

		return seleccionCliente;
	}

	private void crearCliente() throws Exception {

		Cliente cliente = CALLCENTER.getClientes(1);

		int nroSeleccionado = guiValidarInt("Ingrese DNI de cliente:");

		if (cliente.existeCliente(nroSeleccionado))
			alert("DNI: " + nroSeleccionado + " existente en el sistema");
		else {

			cliente = new Cliente("Cliente creado", nroSeleccionado, "Direccion1", "1156937106");
			alert("DNI: " + nroSeleccionado + " dado de alta");
		}

	}

	private void crearServicioCliente() throws Exception {
		Cliente c = seleccionClienteDisponible();
		if (c != null) {
			seleccionarDiaServicio(c);
		}
			
	}

	private void seleccionarDiaServicio(Cliente c) throws Exception {
		String opcionesDia = "Creando servicio para cliente Nro " + c.getNro() + "(" + c.getNombre() + ")\n";
		opcionesDia += "Seleccione un dia:\n";

		ArrayList<Dia> dias = c.getAgenda().getDias();

		for (int i = 0; i < dias.size(); i++) {
			try {
				Dia d = dias.get(i);
				opcionesDia += "\n\t" + ((i + 1) + ") " + d.getDiaSemana() + " " + d.getFechaString());
			} catch (Exception e) {
			}
		}
		int nroDia = guiValidarInt(opcionesDia, 1, dias.size());
		Dia seleccionDia = dias.get(nroDia - 1);

		seleccionParametrosCreacionServicio(c, seleccionDia);
	}

	private void seleccionParametrosCreacionServicio(Cliente c, Dia d) throws Exception {

		ArrayList<FraccionTurno> fraccsTurno = d.obtenerFraccionesTurno();

		String msgDesde = "Seleccione COMIENZO de turno:\n";
		for (int i = 0; i < fraccsTurno.size(); i++) {
			try {
				FraccionTurno ft = fraccsTurno.get(i);
				msgDesde += "\n\t" + ((i + 1) + ") Turno " + ft.getTurno() + " " + ft.getHorario());
			} catch (Exception e) {
			}
		}

		// Se resta 1 porque seleccionamos un item de ArrayList
		int selComienzo = guiValidarInt(msgDesde, 1, fraccsTurno.size()) - 1;

		// AUX seleccion final de turno
		ArrayList<FraccionTurno> fraccsTurnoHasta = new ArrayList<FraccionTurno>();
		for (int i = selComienzo; i < fraccsTurno.size(); i++) {
			fraccsTurnoHasta.add(fraccsTurno.get(i));
		}
		// Fin AUX seleccion final turno

		String msgHasta = "Seleccione FINAL de turno:\n";
		for (int i = 0; i < fraccsTurnoHasta.size(); i++) {
			try {
				FraccionTurno ft = fraccsTurnoHasta.get(i);
				msgHasta += "\n\t" + ((i + 1) + ") Turno " + ft.getTurno() + " " + ft.getHorarioSiguiente());
			} catch (Exception e) {
			}
		}

		// Se resta 1 porque seleccionamos un item de ArrayList
		int selFinal = guiValidarInt(msgHasta, 1, fraccsTurnoHasta.size()) - 1;

		String msgTipoServ = "Seleccione tipo de servicio:\n";
		msgTipoServ += "1) INSTALACION (Requiere al menos 1 hora de turno)\n2) REPARACION";
		int selTipoServ = guiValidarInt(msgTipoServ, 1, 2);

		// Confeccion parametros
		Date fecha = d.getFecha();
		FraccionTurno ftDesde = fraccsTurno.get(selComienzo);
		FraccionTurno ftHasta = fraccsTurnoHasta.get(selFinal);
		TipoServicio ts = selTipoServ == 1 ? TipoServicio.INSTALACION : TipoServicio.REPARACION;

		// Confirmacion de datos
		String msgConfirm = "Confirma creacion de servicio para cliente nro " + c.getNro() + " (" + c.getNombre() + ")";
		msgConfirm += "\nDia: " + d.getDiaSemana() + " " + d.getFechaString();
		msgConfirm += "\nHorario: " + ftDesde.getHorario() + "-" + ftHasta.getHorarioSiguiente();
		msgConfirm += "\nTipo de servicio: " + ts;

		if (confirm(msgConfirm)) {
			Servicio s = CALLCENTER.crearServicio(fecha, ts, ftDesde, ftHasta);
			alert("Creado servicio nro " + s.getNro());

			CALLCENTER.asignarServicio(s, c);
			alert("Servicio nro " + s.getNro() + " asignado a cliente nro " + c.getNro());
			editarServicio(s);
			return;
		}
		throw new GuiException("Cancelada creacion de servicio");
	}

	private void seleccionServicioCC() throws Exception {
		String opciones = "Seleccione servicio por su numero:";
		ArrayList<Servicio> sPendientes = CALLCENTER.getServiciosPendientes();

		if (sPendientes.size() == 0) {
			throw new AsignacionException("No hay servicios pendientes");
		}

		for (Servicio s : sPendientes) {
			boolean faltaTecnico = s.getTecnicos().size() == 0, faltaCliente = s.getCliente() == null;

			// varias lineas para legibilidad
			opciones += "\n\t" + s.getNro() + ") ";
			opciones += DateAux.getInstance().getNombreDiaSemana(s.getFecha()) + " ";
			opciones += DateAux.getInstance().getDateString(s.getFecha());
			opciones += " [" + s.getHorarioServicio() + "] - ";
			opciones += (faltaTecnico || faltaCliente) ? "Pendiente: " : "Listo para liberar";
			opciones += faltaTecnico ? "Tecnico" : "";
			opciones += (faltaTecnico && faltaCliente) ? ", " : "";
			opciones += faltaCliente ? "Cliente" : "";
		}

		int opcionServicio = guiValidarInt(opciones);

		Servicio servSeleccionado = CALLCENTER.getServiciosPendientes(opcionServicio);

		if (servSeleccionado == null) {
			throw new ValorException("Seleccion no valida");
		}
		editarServicio(servSeleccionado);
	}

	private void editarServicio(Servicio s) throws Exception {
		String opciones = "Servicio:\n" + s.toStringShorter();
		opciones += "\n\nSELECCIONE:";
		opciones += "\n\t1) Asignar cliente" + (s.getCliente() == null ? "" : " (Ya tiene cliente)");
		opciones += "\n\t2) Asignar tecnico <Tecnicos asignados: " + s.getTecnicos().size() + ">";
		opciones += "\n\t3) Enviar este servicio a los tecnicos <Estado actual: " + s.getEstadoServicio() + ">";
		opciones += "\n\t4) Cancelar servicio";

		int opcion = guiValidarInt(opciones, 1, 4);

		if (opcion == 1) {
			editarClienteServicio(s);
		} else if (opcion == 2) {
			editarTecnicoServicio(s);
		} else if (opcion == 3) {
			enviarServicioATecnicos(s);
			return;
		} else if (opcion == 4) {
			cancelarServicio(s);
			return;
		}

		// Volver a mostrar el menu del servicio tras accion no terminal
		editarServicio(s);
	}

	private void editarClienteServicio(Servicio s) throws Exception {
		if (s.getCliente() != null) {
			throw new AsignacionException("El servicio ya tiene cliente");
		}

		Cliente c = seleccionClienteDisponible();

		if (c == null) {
			throw new ValorException("Seleccion no valida");
		}

		String opciones = "Confirma asignar a cliente nro " + c.getNro() + " (" + c.getNombre() + ")";
		opciones += "El servicio nro " + s.getNro() + " (" + DateAux.getInstance().getDateString(s.getFecha()) + " - "
				+ s.getHorarioServicio() + ")";

		if (confirm(opciones)) {
			CALLCENTER.asignarServicio(s, c);
			alert("Servicio nro " + s.getNro() + " asignado a cliente nro " + c.getNro());
			return;
		}

		throw new GuiException("Asignacion a cliente cancelada");
	}

	private void editarTecnicoServicio(Servicio s) throws Exception {
		Tecnico t = seleccionTecnicoDisponible(s);

		if (confirm("Asignar tecnico " + t.getNombre() + " al servicio " + s.getNro() + "?")) {
			CALLCENTER.asignarServicio(s, t);
			alert("Tecnico " + t.getNombre() + " asignado al servicio nro " + s.getNro());
		} else {
			throw new GuiException("Cancelado por usuario");
		}
	}

	private void enviarServicioATecnicos(Servicio s) throws Exception {
		if (confirm("Enviar servicio nro " + s.getNro() + " a los tecnicos?")) {
			CALLCENTER.liberarServicioCallcenter(s);
			alert("Servicio nro " + s.getNro() + " enviado a los tecnicos!");
		} else {
			throw new GuiException("Cancelado por usuario");
		}
	}

	private void cancelarServicio(Servicio s) throws Exception {
		if (confirm("Cancelar servicio nro " + s.getNro() + "?")) {
			CALLCENTER.cancelarServicio(s);
			alert("Servicio nro " + s.getNro() + " ahora en estado " + s.getEstadoServicio());
		} else {
			throw new GuiException("No se cancelo el servicio");
		}
	}

	private Articulo seleccionArticulo() throws Exception {
		String opciones = "Seleccione articulo por su SKU:";
		ArrayList<Articulo> articulos = CALLCENTER.getArticulos();

		for (Articulo a : articulos) {
			opciones += "\n\t" + a.getSKU() + ") " + a.getDescripcion() + " - Stock: " + a.getStock();
		}

		int opcionArticulo = guiValidarInt(opciones);
		Articulo articuloSeleccionado = CALLCENTER.getArticulos(opcionArticulo);

		if (articuloSeleccionado == null) {
			throw new ValorException("Seleccion no valida");
		}
		return articuloSeleccionado;
	}

	private void edicionStockArticulo() throws Exception {
		Articulo a = seleccionArticulo();

		String opciones = "Articulo SKU " + a.getSKU() + ": " + a.getDescripcion();
		opciones += "\nIngrese nuevo stock <Actual: " + a.getStock() + ">";

		int stock = a.getStock();
		int nuevoStock = guiValidarInt(opciones, 0);

		CALLCENTER.setStockArticulo(a, nuevoStock);
		alert("Stock de " + a.getDescripcion() + " actualizado: " + stock + " => " + nuevoStock);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		try {
			if (src == btnNewServicio) {
				crearServicioCliente();
			} else if (src == btnGestionServicios) {
				seleccionServicioCC();
			} else if (src == btnGestionArticulo) {
				edicionStockArticulo();
			} else if (src == btnCrearCliente) {
				crearCliente();
			}
		} catch (Exception exception) {
			guiExceptionHandler(exception);
		}
	}

}

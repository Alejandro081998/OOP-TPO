package gui.usuarios;

import javax.swing.JPanel;

import personas.Admin;
import personas.Seniority;

import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import java.awt.BorderLayout;

import comercial.articulos.Articulo;
import excepciones.GuiException;

import java.awt.GridLayout;

@SuppressWarnings("serial")
public class GuiAdmin extends GuiUsuarioBase {
	private final Admin ADMIN;

	private JPanel pPrincipal;
	private JPanel pLayout;
	private JPanel subPLayout;

	private JButton btnCostoCombustible;
	private JButton btnCostoViaje;
	private JButton btnStockCostoArticulos;
	private JButton btnValorHoraTecnicos;

	public GuiAdmin(Admin admin) {
		super("Bienvenido " + admin.getNombre() + "<ADMINISTRADOR>");
		this.ADMIN = admin;

		pPrincipal = new JPanel();
		add(pPrincipal, BorderLayout.CENTER);
		pPrincipal.setLayout(new GridLayout(0, 3, 0, 0));

		// Estetico
		pPrincipal.add(new JLabel(""));

		pLayout = new JPanel();
		pPrincipal.add(pLayout);
		pLayout.setLayout(new GridLayout(3, 0, 0, 0));

		// Estetico
		pLayout.add(new JLabel(""));

		subPLayout = new JPanel();
		pLayout.add(subPLayout);
		subPLayout.setLayout(new GridLayout(0, 1, 0, 0));

		btnCostoCombustible = new JButton("Editar costo combustible");
		btnCostoCombustible.addActionListener(this);
		subPLayout.add(btnCostoCombustible);
		// Estetico
		subPLayout.add(new JLabel(""));

		btnCostoViaje = new JButton("Editar costo viaje");
		btnCostoViaje.addActionListener(this);
		subPLayout.add(btnCostoViaje);
		// Estetico
		subPLayout.add(new JLabel(""));

		btnStockCostoArticulos = new JButton("Editar stock y costo Articulos");
		btnStockCostoArticulos.addActionListener(this);
		subPLayout.add(btnStockCostoArticulos);
		// Estetico
		subPLayout.add(new JLabel(""));

		btnValorHoraTecnicos = new JButton("Editar valor hora tecnicos");
		btnValorHoraTecnicos.addActionListener(this);
		subPLayout.add(btnValorHoraTecnicos);
	}

	private void invocarEdicionCombustible() throws Exception {
		double nuevoCC = guiValidarDouble(
				"Ingrese nuevo costo para combustible <ACTUAL: " + ADMIN.getCostoCombustible() + ">");

		ADMIN.modificarCostoCombustible(nuevoCC);
		alert("Costo combustible cambiado a " + ADMIN.getCostoCombustible());
	}

	private void invocarEdicionViaje() throws Exception {
		double nuevoCV = guiValidarDouble("Ingrese nuevo costo para viaje <ACTUAL: " + ADMIN.getCostoViaje() + ">");

		ADMIN.modificarCostoViaje(nuevoCV);
		alert("Costo viaje cambiado a " + ADMIN.getCostoViaje());
	}

	private void invocarEdicionCHT() throws Exception {
		String opciones = "\n\t1) Junior\n\t2) Semi-Senior\n\t3) Senior";
		int seleccion = guiValidarInt("Seleccione seniority:" + opciones, 1, 3);

		Seniority seleccionSeniority = null;

		switch (seleccion) {
		case 1:
			seleccionSeniority = Seniority.JUNIOR;
			break;
		case 2:
			seleccionSeniority = Seniority.SEMI_SENIOR;
			break;
		case 3:
			seleccionSeniority = Seniority.SENIOR;
			break;
		}

		if (seleccionSeniority == null) {
			throw new GuiException("Seleccion no valida");
		}

		double currentCHT = ADMIN.getCostoHoraTecnico(seleccionSeniority);
		double nuevoCosto = guiValidarDouble(
				"Ingrese nuevo costo por hora para " + seleccionSeniority + "<ACTUAL " + currentCHT + ">");

		ADMIN.setCostoHoraTecnico(seleccionSeniority, nuevoCosto);
		alert("CHT para " + seleccionSeniority + " ahora es " + ADMIN.getCostoHoraTecnico(seleccionSeniority));
	}

	private void invocarEdicionArticulosAdmin() throws Exception {
		ArrayList<Articulo> arts = ADMIN.getArticulos();
		String opciones = "Seleccione un articulo por su SKU:";

		for (Articulo a : arts) {
			opciones += "\n\tSKU: " + a.getSKU() + " | " + a.getDescripcion() + " [COSTO: " + a.getCosto() + ", STOCK: "
					+ a.getStock() + "]";
		}

		int eleccion = guiValidarInt(opciones);

		Articulo seleccionArticulo = ADMIN.getArticulos(eleccion);

		if (seleccionArticulo == null) {
			throw new GuiException("Seleccion no valida");
		}

		String descripcion = seleccionArticulo.getDescripcion();
		int nvoStock = guiValidarInt("Stock de" + descripcion + "<ACTUAL " + seleccionArticulo.getStock() + ">");
		double nvoCosto = guiValidarDouble("Coste de " + descripcion + "<ACTUAL " + seleccionArticulo.getCosto() + ">");
		boolean confirma = confirm("Acepta valores de " + descripcion + "? (S:" + nvoStock + "|C:" + nvoCosto + ")");

		if (confirma) {
			ADMIN.editarCifrasArticulo(seleccionArticulo, nvoStock, nvoCosto);
			alert("Nuevos valores fijados: " + ADMIN.getArticulos(eleccion));
			return;
		}

		alert("Cancelado por usuario");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		try {
			if (src == btnCostoCombustible) {
				invocarEdicionCombustible();
			} else if (src == btnCostoViaje) {
				invocarEdicionViaje();
			} else if (src == btnValorHoraTecnicos) {
				invocarEdicionCHT();
			} else if (src == btnStockCostoArticulos) {
				invocarEdicionArticulosAdmin();
			}
		} catch (Exception exception) {
			guiExceptionHandler(exception);
		}

	}

}

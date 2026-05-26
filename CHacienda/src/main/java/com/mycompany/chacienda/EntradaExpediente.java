/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Sistema de Gestión Hospitalaria
 */
package com.mycompany.chacienda;

/**
 * EntradaExpediente - Nodo de lista enlazada.
 * Representa una consulta médica en el historial del paciente.
 */
public class EntradaExpediente {

    public String fecha;
    public String medico;
    public String motivo;
    public String notas;

    // Puntero para lista enlazada
    public EntradaExpediente siguiente;

    public EntradaExpediente(String fecha, String medico, String motivo, String notas) {
        this.fecha     = fecha;
        this.medico    = medico;
        this.motivo    = motivo;
        this.notas     = notas;
        this.siguiente = null;
    }

    @Override
    public String toString() {
        return "  Fecha   : " + fecha  + "\n"
             + "  Médico  : " + medico + "\n"
             + "  Motivo  : " + motivo + "\n"
             + "  Notas   : " + notas;
    }

    // Para guardar en archivo separado por |
    public String toArchivo() {
        return fecha + "|" + medico + "|" + motivo + "|" + notas;
    }
}

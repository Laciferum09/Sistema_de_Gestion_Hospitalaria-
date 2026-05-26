/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chacienda;

/**
 * Clase Paciente - Nodo base para el árbol BST y la cola de prioridad.
 * Contiene los datos médicos del paciente y punteros para las estructuras.
 */
public class Paciente {

    // Datos básicos del paciente
    public String cedula;
    public String nombre;
    public int edad;
    public String diagnostico;
    public int prioridad; // 1 = Crítico, 2 = Urgente, 3 = Normal

    // PUNTERO para Lista Enlazada / BST
    public Paciente siguiente;

    // PUNTEROS para Árbol BST
    public Paciente izquierdo;
    public Paciente derecho;

    // Constructor completo
    public Paciente(String cedula, String nombre, int edad, String diagnostico, int prioridad) {
        this.cedula      = cedula;
        this.nombre      = nombre;
        this.edad        = edad;
        this.diagnostico = diagnostico;
        this.prioridad   = prioridad;
        this.siguiente   = null;
        this.izquierdo   = null;
        this.derecho     = null;
    }

    // Devuelve la prioridad como texto legible
    public String getPrioridadTexto() {
        switch (prioridad) {
            case 1: return "CRÍTICO";
            case 2: return "URGENTE";
            case 3: return "NORMAL";
            default: return "DESCONOCIDO";
        }
    }

    @Override
    public String toString() {
        return "Cédula: " + cedula
             + " | Nombre: " + nombre
             + " | Edad: " + edad
             + " | Diagnóstico: " + diagnostico
             + " | Prioridad: " + getPrioridadTexto();
    }
}

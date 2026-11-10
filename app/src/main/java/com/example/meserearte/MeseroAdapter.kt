package com.example.meserearte

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class MeseroAdapter(
    private val alCambiar: (Mesero, Boolean) -> Unit,
    private val alVer: (Mesero) -> Unit,
    private val alEditar: (Mesero) -> Unit,
    private val alEliminar: (Mesero) -> Unit
) : RecyclerView.Adapter<MeseroAdapter.MeseroViewHolder>() {

    private val elementos = mutableListOf<Mesero>()

    fun actualizar(nuevos: List<Mesero>) {
        elementos.clear()
        elementos.addAll(nuevos)
        notifyDataSetChanged() // Lista pequeña de demostración.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeseroViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesero, parent, false)
        return MeseroViewHolder(vista)
    }

    override fun getItemCount(): Int = elementos.size

    override fun onBindViewHolder(holder: MeseroViewHolder, position: Int) {
        val mesero = elementos[position]
        holder.nombre.text = mesero.nombre
        holder.sala.text = "Sala: ${mesero.sala}"
        holder.eventos.text = "Eventos participados: ${mesero.eventos}"
        holder.estado.text = if (mesero.activo) "Activo" else "Inactivo"
        holder.interruptor.setOnCheckedChangeListener(null)
        holder.interruptor.isChecked = mesero.activo
        holder.interruptor.setOnCheckedChangeListener { _, marcado ->
            alCambiar(mesero, marcado)
        }
        holder.ver.setOnClickListener { alVer(mesero) }
        holder.editar.setOnClickListener { alEditar(mesero) }
        holder.eliminar.setOnClickListener { alEliminar(mesero) }
    }

    class MeseroViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val nombre: TextView = vista.findViewById(R.id.tvMeseroNombre)
        val sala: TextView = vista.findViewById(R.id.tvMeseroSala)
        val eventos: TextView = vista.findViewById(R.id.tvMeseroEventos)
        val estado: TextView = vista.findViewById(R.id.tvMeseroEstado)
        val interruptor: SwitchMaterial = vista.findViewById(R.id.swMeseroActivo)
        val ver: Button = vista.findViewById(R.id.btnMeseroVer)
        val editar: Button = vista.findViewById(R.id.btnMeseroEditar)
        val eliminar: Button = vista.findViewById(R.id.btnMeseroEliminar)
    }
}

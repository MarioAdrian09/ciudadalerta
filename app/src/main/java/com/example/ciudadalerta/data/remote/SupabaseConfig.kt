package com.example.ciudadalerta.data.remote

/**
 * Datos públicos de conexión al proyecto de Supabase.
 * La publishable key es segura en apps móviles: la protección real son las políticas RLS.
 */
object SupabaseConfig {
    const val URL = "https://qxaqfeoxvpbfcaytsxeq.supabase.co"
    //TODO PONER EL KEY DE LA APP
    const val PUBLISHABLE_KEY = "sb_publishable__K7qHP_yznS9cJrnKFAs8w_QrbMj451"

    // Nombre del bucket de Storage donde se guardan las fotos de los reportes
    const val BUCKET_FOTOS = "fotos-reportes"


    /**
     * Email: mario@gmail.com
     * password: admin123
     */
}

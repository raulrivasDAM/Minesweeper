package es.riberadeltajo.buscaminas_raul_rivas_jeronimo;

public class Mina {
    public String descripcion;

    public int imagen;

    public Mina(String descripcion, int imagen) {
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }


}

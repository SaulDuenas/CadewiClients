<?php

class  Photo_model extends CI_Model {

	function __construct()

	{
		// llamada al contructor del modelo
		parent::__construct();
	}


	public function get_head($address = null,$photoId = null) {
		
		return $this->_get_head($address,$photoId);
	}


	public function get_bynary($photoId = null) {

		return $this->_get_bynary($photoId);
	}


	public function save($data){

		$this->db->set($data)->insert('imagenes');

		$lastId =  null;
		$lastId = $this->db->insert_id();

		if ($lastId != null){	
				$query=$this->_get_head($data['Direccion'],$lastId);
				return $query; 
			}

		return false;
		//if ($this->db->affected_rows() === 1){
			
		//}

		//return false;

	}

	public function update($PhotoId,$Data){

		$query = false;

		$this->db->set($Data)->where('Imagen',$PhotoId)->update('imagenes');

		//if ($this->db->affected_rows() === 1){
			
				
		
		//}

		$query=$this->_get_bynary($PhotoId);
		return $query; 
	}


	private function _get_head($address = null,$photoId= null){

		if(!is_null($address)) {

			$this->db->select("A.Imagen,A.Direccion,A.Cliente,NULL as 'Bynary',A.Nombre,A.Ruta,IFNULL(A.Nota,'') as 'Nota',A.FechaHora,A.Estatus ");
			$this->db->from("imagenes A");
			$this->db->group_start();
			$this->db->where("A.Direccion", $address );
			$this->db->group_end();
			$this->db->group_start();
			$this->db->where("A.Imagen", $photoId );
			$this->db->or_where("0", $photoId );
			$this->db->group_end();
			$this->db->order_by("A.FechaHora", "DESC");
	        $photo = $this->db->get();
	
			if($photo->num_rows() > 0) {
				return $photo->result_array();
			}

			return false;	
		}
		
		//$query = $this->db->select('*')->from('usuarios')->get();
		$query = $this->db->query($sql,array('',''));

		if($query->num_rows() > 0){
			return $query->result_array();
		}
		
		return false;

	}



	private function _get_bynary($photoId = null) {

		if(!is_null($photoId)) {

			$this->db->select("A.Imagen,A.Direccion,A.Cliente,TO_BASE64(A.Binario) as Binario,A.Nombre,A.Ruta,A.Nota,A.FechaHora ");
			$this->db->from("imagenes A");
			$this->db->group_start();
			$this->db->where("A.Imagen", $photoId );
		    $this->db->group_end();
			$this->db->order_by("A.FechaHora", "DESC");
	        $photo = $this->db->get();

			if($query->num_rows() > 0) {
				return $query->result_array();
			}
		
		}

		return false;

	}

}
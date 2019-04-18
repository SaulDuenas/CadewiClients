<?php

class  Address_model extends CI_Model {

	function __construct() {
	    // llamada al contructor del modelo
		parent::__construct();
	}

	public function get($client = null) { 

		return $this->_get($client);

	}


	public function save($data) {

		$this->db->set($this->_setAddress($data))->insert('direcciones');

		if ($this->db->affected_rows() === 1){

			$lastId = $this->db->insert_id();
			return $lastId; 
		}
		return false;
	}


	private function _get($client = null) {

		if(!is_null($client)){

			$query = $this->db->select('*')->from('direcciones')->where('cliente',$client)->get();

			if($query->num_rows() > 0){

				return $query->result_array();

			}
			return false;
        }

		$query = $this->db->select('*')->from('direcciones')->get();

		if($query->num_rows() > 0){
			return $query->result_array();
		}

		return false;

    }



	private function _setAddress($data) {

		return array(	'Cliente' => $data['Client'],
						'Pais' => $data['Nation'],
						'CodigoPostal' => $data['Zipcode'],
						'Calle' => $data['Street'],
						'Municipio' => $data['Municipality'],
						'Estado' => $data['State'],
						'Ciudad' => $data['City'],
						'Colonia' => $data['Colony'],
						'Detalles' => $data['Detail'],
						'Longitud' => $data['longitude'],
						'Latitud' => $data['latitude'],
						'LocEditable' => '0'
					);

	} 

}
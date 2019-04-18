<?php
class  zipCode_model extends CI_Model{

	function __construct() {
	    // llamada al contructor del modelo
		parent::__construct();
	}


	public function get($zipcode = null) {
		return $this->_get($zipcode);
	}


	private function _get($zipcode = null){
		
		if(!is_null($zipcode)){
			$query = $this->db->select("D_Codigo,D_Asenta,D_Tipo_Asenta,D_mnpio,D_estado,D_ciudad,C_estado,C_tipo_asenta,C_mnpio,Id_asenta_cpcons,D_zona,C_cve_ciudad")->from('cadewigr_catalogue.directoriopostal')->where('D_codigo',$zipcode)->get();

			if($query->num_rows() > 0){

				return $query->result_array();
			}

			return false;
		}

		return false;
	}

}
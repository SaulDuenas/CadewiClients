<?php
class Parameter_model extends CI_Model{

	function __construct()
	{
	    // llamada al contructor del modelo
		parent::__construct();
		
	}


	public function get($parameter = null,$category = null)
	{
		
		return $this->_get($parameter,$category);
	}


    public function getStringValue ($parameter = null,$category = null) { 

   		$parameter = $this->_get($parameter,$category);

   		if ($parameter  != false) { 
   			return $parameter->sValor;
   		}
   		else{
   			return null;
   		}
   		 
    }


    public function getNumericValue ($parameter = null,$category = null) { 

   		$parameter = $this->_get($parameter,$category);

   		if ($parameter  != false) { 
   			return $parameter->nValor;
   		}
   		else{
   			return null;
   		}
   		 
    }


 	public function getDateValue ($parameter = null,$category = null) { 

   		$parameter = $this->_get($parameter,$category);

   		if ($parameter  != false) { 
   			return $parameter->dValor;
   		}
   		else{
   			return null;
   		}
   	}



	private function _get($parameter = null,$category = null) {
		
			
		$this->db->select("A.Categoria,A.Parametro,A.nValor,A.sValor,A.dValor,A.Descripcion,A.Tipo ");
		$this->db->from("parametros A");
	
		$this->db->group_start();
		$this->db->where("A.Categoria", $category );
		$this->db->where("A.Parametro",$parameter);
		$this->db->group_end();
		
        $parameter = $this->db->get();

       // echo $this->db->last_query();

		if($parameter->num_rows() > 0) {
			return $parameter->row();
		}
		
		return false;

	} 

}

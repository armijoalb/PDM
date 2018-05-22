using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class trigger_mummy : MonoBehaviour {

	private float damage = 10f;

	void OnTriggerEnter(Collider col){
		Debug.Log (col.tag);
		if (col.CompareTag ("Player")) {
			Debug.Log ("mummy: te mato hijoputa");
			var player = col.gameObject.GetComponent<Health_controller> ();
			player.looseHealth (damage);
		}
	}
}

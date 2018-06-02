using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class trigger_mummy : MonoBehaviour {

	public float damage = 10f;
	public float damagePerSecond = 5f;

	void OnCollisionEnter(Collision col){
		if (col.collider.CompareTag ("Player")) {
			var player = col.gameObject.GetComponent<Health_controller> ();
			player.looseHealth (damage);
		}
	}


	void OnCollisionExit(Collision col){
		if (col.collider.CompareTag ("Player")) {
			var player = col.gameObject.GetComponent<Health_controller> ();
			player.looseHealth (damagePerSecond);
		}
	}
		
}

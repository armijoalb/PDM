using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class mummy_health : MonoBehaviour {

	private float maxHealth = 2f;
	private float currentHealth;

	void Awake(){
		currentHealth = maxHealth;
	}

	public void  looseHealth(float loosed){
		currentHealth -= loosed;
		Debug.Log ("big mummy: " + currentHealth);
		if (currentHealth <= 0) {
			Debug.Log ("big mummy: dying");
			Destroy (gameObject);
		}
	}
}

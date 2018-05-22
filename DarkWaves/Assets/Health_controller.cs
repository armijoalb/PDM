using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Health_controller : MonoBehaviour {

	private Animator animator;
	public bool isDead = false;
	private float maxHealth = 100f;
	private float currentHealth;
	public bool oneDead = false;

	void Awake(){
		animator = GetComponent<Animator> ();
		currentHealth = maxHealth;
		isDead = false;

	}
	
	// Update is called once per frame
	void Update () {
		animator.SetBool ("isDead", isDead);

	

		if (currentHealth <= 0) {
			isDead = true;
			oneDead = true;
		}

		if (oneDead) {
			isDead = false;
		}

	}

	public void looseHealth(float healthLoosed){
		Debug.Log ("Loosing health");
		currentHealth -= healthLoosed;
		Debug.Log ("current health: " + currentHealth);
	}

	public float getCurrentHealth(){
		return currentHealth;
	}

}

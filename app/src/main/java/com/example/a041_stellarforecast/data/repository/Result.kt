package com.example.a041_stellarforecast.data.repository

/**
 * Ce code définit une classe appelée  Result  qui est utilisée pour représenter le résultat d une
 * opération qui peut réussir, échouer ou être en cours d exécution.
 *
 * Voici les trois états que peut renvoyer la classe  Result  :
 *
 *     Loading  (Chargement) :
 *     Il s agit d un objet unique représentant l état où l opération est en cours d exécution. Cela
 *     peut être utile pour afficher une indication visuelle à l utilisateur, comme une icône de 
 *     chargement.
 *
 *     Failure  (Échec) :
 *     C est une classe de données qui représente l état où l opération a échoué. Elle peut contenir
 *     un message décrivant l erreur survenue.
 *
 *     Success  (Succès) :
 *     C est une classe de données générique qui stocke le résultat de l opération en cas de succès.
 *     Elle prend un type générique R pour permettre de représenter différents types de résultats.
 *
 * Le mot-clé  sealed  indique que toutes les sous-classes de  Result  doivent être définies dans le
 * même fichier. Cela offre une exhaustivité dans la gestion des résultats, car toutes les 
 * possibilités sont répertoriées dans un seul endroit.
 *
 */
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Failure(
        val message: String? = null,
    ) : Result<Nothing>()

    data class Success<out R>(val value: R) : Result<R>()
}
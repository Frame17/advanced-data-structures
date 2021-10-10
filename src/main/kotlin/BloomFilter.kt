import java.util.*
import kotlin.math.ln

/**
 * Simple generic bloom filter implementation using one default Java hash function.
 * Given fpp = (1 - e ^ (-(numHashes - size) / numBits)) ^ numHashes (Rocca &amp; Serrano, Advanced algorithms
 * and Data Structures 2021) and solving for numBits we can find bitSet size to satisfy the fpp constraint.
 */
class BloomFilter<T>(estimatedSize: Int, fpp: Double = 0.01) {
    private val bitSet: BitSet

    init {
        this.bitSet = BitSet((-estimatedSize / ln(1 - fpp)).toInt())
    }

    operator fun get(element: T): Boolean = bitSet[element.hash()]

    fun put(element: T) = bitSet.set(element.hash())

    private fun T.hash() = hashCode().mod(bitSet.size())
}